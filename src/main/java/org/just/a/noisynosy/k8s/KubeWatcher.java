package org.just.a.noisynosy.k8s;

import org.just.a.noisynosy.analyzer.PodAnalyzer;
import org.just.a.noisynosy.analyzer.analysis.RuleAnalysis;
import org.just.a.noisynosy.handler.Handler;
import org.just.a.noisynosy.notifier.Notifier;
import org.just.a.noisynosy.rules.Rule;
import org.just.a.noisynosy.rules.WatchFor;
import org.just.a.noisynosy.utils.AnalyzerUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import io.fabric8.kubernetes.api.model.Pod;

@Component
public class KubeWatcher implements Closeable {

  private static final Logger LOGGER = Logger.getLogger(KubeWatcher.class.getName());

  private final WatchFor config;
  private final KubeClient client;

  private final Map<String, List<PodAnalyzer>> watches;
  private final Map<String, Pod> podsBeingWatched;

  private final List<Handler> handlers;
  private final List<Notifier> notifiers;

  public KubeWatcher(WatchFor config, List<Handler> handlers,
      List<Notifier> notifiers, KubeClient client) {
    this.config = config;
    this.handlers = handlers;
    this.notifiers = notifiers;
    this.client = client;

    this.watches = new HashMap<>();
    this.podsBeingWatched = new HashMap<>();
  }

  @Override
  public void close() throws IOException {
    this.client.close();

    for (final Map.Entry<String, List<PodAnalyzer>> entry : watches.entrySet()) {
      for (final PodAnalyzer analyzer : entry.getValue()) {
        analyzer.close();
      }
    }
  }

  @Scheduled(initialDelay = 60000, fixedDelayString = "${k8s.watch.scheduled:60000}")
  public void peekALook() {
    LOGGER.log(Level.INFO, "Taking a look at the cluster situation..");

    for (final Map.Entry<String, List<PodAnalyzer>> entry : watches.entrySet()) {
      final Pod pod = podsBeingWatched.get(entry.getKey());
      final List<RuleAnalysis> newlySatisfiedRules = new ArrayList<>();
      entry.getValue().forEach(a -> newlySatisfiedRules.addAll(a.checkResults()));

      if (!newlySatisfiedRules.isEmpty()) {
        final Map<String, String> handlerActions = new HashMap<>();
        handlers.forEach(handler -> {
          final String key = handler.getNotifyKey();
          final String val = handler.handle(pod, newlySatisfiedRules);
          handlerActions.put(key, val);
        });
        notifiers.forEach(notifier -> notifier.notify(pod, newlySatisfiedRules,
            handlerActions));
      }
    }

    LOGGER.log(Level.INFO, "Taking a look at the cluster situation.. done");
  }

  public void resetAnalysis(String namespace, String name, String ruleName) {
    final String podKey = KubeUtils.getPodKey(namespace, name);
    if (watches.containsKey(podKey)) {
      watches.get(podKey).forEach(a -> a.resetAnalysis(ruleName));
    }
  }

  @PostConstruct
  public void setup() {
    config.checkValidity();

    final List<Rule> rules = config.getAllRules();
    if (rules != null && !rules.isEmpty()) {
      startWatchingPodsForRules(rules);
    }
  }

  @Scheduled(initialDelay = 600000, fixedDelayString = "${k8s.watch.setup:600000}")
  public void updateWatches() {
    LOGGER.log(Level.INFO, "Updating watches..");

    final List<Rule> rules = config.getAllRules();
    if (rules != null && !rules.isEmpty()) {
      startWatchingPodsForRules(rules);
    }

    LOGGER.log(Level.INFO, "Updating watches.. done");
  }

  private void beginAnalysisFor(String podKey) {
    LOGGER.log(Level.INFO, () -> String.format(
        "Beginning analysis for pod %s", podKey));
    watches.get(podKey).forEach(PodAnalyzer::beginAnalysis);
  }

  private void cleanupUnusedWatches(Set<String> keysFound) {
    final List<String> keysToRemove = new ArrayList<>();
    for (final Entry<String, Pod> entry : podsBeingWatched.entrySet()) {
      if (!keysFound.contains(entry.getKey())) {
        keysToRemove.add(entry.getKey());
      }
    }

    keysToRemove.forEach(this::stopHandlingPod);
  }

  private void closeWatchFor(String podKey) {
    watches.get(podKey).forEach(a -> {
      try {
        a.close();
      } catch (final IOException e) {
        LOGGER.log(Level.SEVERE, "Unable to close a watcher for " + podKey, e);
      }
    });
    LOGGER.log(Level.INFO, () -> "Stopping all watchers for " + podKey);
  }

  private void handlePod(Pod pod, List<Rule> rules) {
    final String podKey = KubeUtils.getPodKey(pod);
    final String podUid = KubeUtils.getPodUid(pod);

    if (!podsBeingWatched.containsKey(podKey)) {
      // as we don't know about this Pod, begin analysis
      startHandlingPod(podKey, pod, rules);
    } else if (!KubeUtils.getPodUid(podsBeingWatched.get(podKey)).equals(podUid)) {
      // as we know about this Pod but the UID changed, restart analysis
      stopHandlingPod(podKey);
      startHandlingPod(podKey, pod, rules);
    }
  }

  private void startHandlingPod(String podKey, Pod pod, List<Rule> rules) {
    final List<PodAnalyzer> analyzers = AnalyzerUtils.setupAnalysis(client, pod, rules);

    watches.put(podKey, analyzers);
    podsBeingWatched.put(podKey, pod);
    beginAnalysisFor(podKey);
  }

  private void startWatchingPodsForRules(List<Rule> rules) {
    final List<Pod> pods = client.getPods();
    final Set<String> keysFound = new HashSet<>();

    for (final Pod pod : pods) {
      keysFound.add(KubeUtils.getPodKey(pod));

      final List<Rule> validRules = rules.stream()
          .filter(f -> f.doesItSelect(pod))
          .collect(Collectors.toList());
      if (!validRules.isEmpty()) {
        handlePod(pod, validRules);
      }
    }

    cleanupUnusedWatches(keysFound);
  }

  private void stopHandlingPod(String podKey) {
    closeWatchFor(podKey);
    watches.remove(podKey);
    podsBeingWatched.remove(podKey);
  }

}
