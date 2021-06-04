package org.just.a.noisynosy.analyzer.status;

import org.just.a.noisynosy.analyzer.PodAnalyzer;
import org.just.a.noisynosy.k8s.KubeClient;
import org.just.a.noisynosy.rules.Rule;

import java.io.IOException;
import java.util.List;

import io.fabric8.kubernetes.api.model.Pod;

public class PodStatusAnalyzer extends PodAnalyzer {

  public PodStatusAnalyzer(KubeClient client, Pod pod, List<Rule> rules) {
    super(client, pod, rules);
  }

  @Override
  public void close() throws IOException {
    // there is nothing to close in this implementation
  }

  @Override
  protected void progressAnalysis() {
    final Pod updatedPod = getClient().updatePodInfo(getPod());
    if (updatedPod != null) {
      getAnalysis().stream()
          .filter(a -> !a.wasSatisfied())
          .forEach(a -> a.isSatisfiedWith(updatedPod));
    }
  }

  @Override
  protected void setupAnalysis() {
    getAnalysis().forEach(a -> a.isSatisfiedWith(getPod()));
    resetAnalysis(null);
  }

}
