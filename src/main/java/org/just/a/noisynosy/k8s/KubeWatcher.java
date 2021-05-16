/*
 *  * Copyright 2021 Thales Italia spa.  *   * This program is not yet licensed
 * and this file may not be used under any  * circumstance.  
 */
package org.just.a.noisynosy.k8s;

import org.just.a.noisynosy.analyzer.PodAnalyzer;
import org.just.a.noisynosy.analyzer.analysis.RuleAnalysis;
import org.just.a.noisynosy.handler.Handler;
import org.just.a.noisynosy.notifier.Notifier;
import org.just.a.noisynosy.rules.Rule;
import org.just.a.noisynosy.rules.WatchFor;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import io.fabric8.kubernetes.api.model.Pod;

@Component
public class KubeWatcher implements Closeable {

  private final WatchFor config;
  private final KubeClient client;

  private final Map<String, PodAnalyzer> watches;

  private final List<Handler> handlers;
  private final List<Notifier> notifiers;

  public KubeWatcher(WatchFor config, List<Handler> handlers,
      List<Notifier> notifiers, KubeClient client) {
    this.config = config;
    this.handlers = handlers;
    this.notifiers = notifiers;
    this.client = client;

    this.watches = new HashMap<>();
  }

  @Override
  public void close() throws IOException {
    this.client.close();

    for (final Map.Entry<String, PodAnalyzer> entry : watches.entrySet()) {
      entry.getValue().close();
    }
  }

  @Scheduled(initialDelay = 60000, fixedDelayString = "${k8s.watch.scheduled:60000}")
  public void peekALook() {
    for (final Map.Entry<String, PodAnalyzer> entry : watches.entrySet()) {
      final List<RuleAnalysis> newlySatisfiedRules = entry.getValue().checkResults();

      if (!newlySatisfiedRules.isEmpty()) {
        handlers.forEach(handler -> handler.handle(entry.getValue().getPod(),
            newlySatisfiedRules));
        notifiers.forEach(notifier -> notifier.notify(entry.getValue().getPod(),
            newlySatisfiedRules));
      }
    }
  }

  @PostConstruct
  public void setup() {
    config.checkValidity();

    if (config.getLogRules() != null) {
      startWatchingPodsForRules(config.getLogRules());
    }
  }

  private void startWatchingPodsForRules(List<Rule> rules) {
    final List<Pod> pods = client.getPods();
    for (final Pod pod : pods) {
      final String podKey = KubeUtils.getPodKey(pod);

      if (!watches.containsKey(podKey)) {
        final List<Rule> validRules = rules.stream()
            .filter(f -> f.doesItSelect(pod))
            .collect(Collectors.toList());

        if (!validRules.isEmpty()) {
          watches.put(podKey, new PodAnalyzer(client, pod, validRules));
          watches.get(podKey).beginAnalysis();
        }
      }
    }
  }

}
