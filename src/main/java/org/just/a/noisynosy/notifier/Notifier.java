package org.just.a.noisynosy.notifier;

import org.just.a.noisynosy.analyzer.analysis.RuleAnalysis;

import java.util.List;
import java.util.Map;

import io.fabric8.kubernetes.api.model.Pod;

public interface Notifier {

  public void notify(Pod pod, List<RuleAnalysis> analysis,
      Map<String, String> handlerActions);

  public void notify(Pod pod, RuleAnalysis analysis,
      Map<String, String> handlerActions);
}
