package org.just.a.noisynosy.handler;

import org.just.a.noisynosy.analyzer.analysis.RuleAnalysis;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

import io.fabric8.kubernetes.api.model.Pod;

@Component
@ConditionalOnProperty(
    value = "handlers.reset-analysis.enabled",
    havingValue = "true")
public class ResetAnalysisHandler implements Handler {

  public static final String RESET_ACTION = "{{reset-action}}";

  @Value("${general.base-url}")
  private String baseUrl;

  @Override
  public String getNotifyKey() {
    return RESET_ACTION;
  }

  @Override
  public String handle(Pod pod, List<RuleAnalysis> analysis) {
    if (analysis.size() == 1) {
      return handle(pod, analysis.get(0));
    }

    final String namespace = pod.getMetadata().getNamespace();
    final String name = pod.getMetadata().getName();

    return String.format("%s/api/pods/reset/%s/%s", baseUrl, namespace, name);
  }

  @Override
  public String handle(Pod pod, RuleAnalysis analysis) {
    final String namespace = pod.getMetadata().getNamespace();
    final String name = pod.getMetadata().getName();
    final String ruleName = analysis.getRule().getName();

    return String.format("%s/api/pods/reset/%s/%s/%s", baseUrl, namespace, name, ruleName);
  }

}
