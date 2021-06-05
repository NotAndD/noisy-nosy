package org.just.a.noisynosy.analyzer;

import org.just.a.noisynosy.analyzer.analysis.RuleAnalysis;
import org.just.a.noisynosy.k8s.KubeClient;
import org.just.a.noisynosy.rules.Rule;

import java.io.Closeable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import io.fabric8.kubernetes.api.model.Pod;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class PodAnalyzer implements Closeable {

  private static final Logger LOGGER = Logger.getLogger(PodAnalyzer.class.getName());

  private final KubeClient client;
  private final Pod pod;
  private final List<RuleAnalysis> analysis;

  public PodAnalyzer(KubeClient client, Pod pod, List<Rule> rules) {
    this.pod = pod;
    this.client = client;
    this.analysis = rules.stream()
        .map(RuleAnalysis::new).collect(Collectors.toList());
  }

  public void beginAnalysis() {
    setupAnalysis();
  }

  public List<RuleAnalysis> checkResults() {
    progressAnalysis();

    final List<RuleAnalysis> result = analysis.stream()
        .filter(a -> !a.wasProgressNoticed())
        .filter(RuleAnalysis::isSatisfied)
        .collect(Collectors.toList());

    if (!result.isEmpty()) {
      LOGGER.log(Level.INFO, () -> String.format("Rule / Rules satisfied for pod %s/%s",
          getPodNamespace(), getPodName()));
      result.forEach(RuleAnalysis::noticeProgress);
    }

    return result;
  }

  public void resetAnalysis(String ruleName) {
    analysis.forEach(a -> {
      if (ruleName == null || ruleName.equals(a.getRule().getName())) {
        a.reset();
      }
    });
  }

  protected String getPodName() {
    return pod.getMetadata().getName();
  }

  protected String getPodNamespace() {
    return pod.getMetadata().getNamespace();
  }

  protected abstract void progressAnalysis();

  protected abstract void setupAnalysis();

}
