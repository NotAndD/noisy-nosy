package org.just.a.noisynosy.notifier;

import org.just.a.noisynosy.analyzer.analysis.RuleAnalysis;

import java.util.List;

import io.fabric8.kubernetes.api.model.Pod;

public abstract class Notifier {

  public void notify(Pod pod, List<RuleAnalysis> analysis) {
    analysis.forEach(a -> notify(pod, a));
  }

  public abstract void notify(Pod pod, RuleAnalysis analysis);
}
