package org.just.a.noisynosy.utils;

import org.just.a.noisynosy.analyzer.PodAnalyzer;
import org.just.a.noisynosy.analyzer.log.PodLogAnalyzer;
import org.just.a.noisynosy.k8s.KubeClient;
import org.just.a.noisynosy.rules.Rule;
import org.just.a.noisynosy.rules.RuleType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import io.fabric8.kubernetes.api.model.Pod;

public final class AnalyzerUtils {

  public static List<PodAnalyzer> setupAnalysis(KubeClient client, Pod pod, List<Rule> rules) {
    final Map<RuleType, List<Rule>> rulesByType = groupRulesByType(rules);
    final List<PodAnalyzer> result = new ArrayList<>();

    for (final Entry<RuleType, List<Rule>> entry : rulesByType.entrySet()) {
      result.add(getAnalyzerFor(entry.getKey(), client, pod, entry.getValue()));
    }
    return result;
  }

  private static PodAnalyzer getAnalyzerFor(RuleType type, KubeClient client, Pod pod,
      List<Rule> rules) {
    switch (type) {
      case LOG:
        return new PodLogAnalyzer(client, pod, rules);
      default:
        throw new RuntimeException("Looks like some implementation is missing here..");
    }
  }

  private static Map<RuleType, List<Rule>> groupRulesByType(List<Rule> rules) {
    final Map<RuleType, List<Rule>> result = new HashMap<>();
    rules.forEach(rule -> {
      final RuleType ruleType = rule.ruleType();
      if (!result.containsKey(ruleType)) {
        result.put(ruleType, new ArrayList<>());
      }
      result.get(ruleType).add(rule);
    });

    return result;
  }

  private AnalyzerUtils() {
    super();
  }

}
