package org.just.a.noisynosy.handler;

import org.just.a.noisynosy.analyzer.analysis.RuleAnalysis;

import java.util.List;

import io.fabric8.kubernetes.api.model.Pod;

public interface Handler {

  public void handle(Pod pod, List<RuleAnalysis> analysis);

  public void handle(Pod pod, RuleAnalysis analysis);
}
