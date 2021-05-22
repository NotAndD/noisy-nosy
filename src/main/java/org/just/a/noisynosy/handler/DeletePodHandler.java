package org.just.a.noisynosy.handler;

import org.just.a.noisynosy.analyzer.analysis.RuleAnalysis;
import org.just.a.noisynosy.k8s.KubeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

import io.fabric8.kubernetes.api.model.Pod;

@Component
@ConditionalOnProperty(
    value = "handlers.delete-pod.enabled",
    havingValue = "true")
public class DeletePodHandler implements Handler {

  public static final String DELETE_ACTION = "{{delete-action}}";

  private final KubeService service;

  @Value("${general.base-url}")
  private String baseUrl;

  public DeletePodHandler(KubeService service) {
    this.service = service;
  }

  @Override
  public String getNotifyKey() {
    return DELETE_ACTION;
  }

  @Override
  public String handle(Pod pod, List<RuleAnalysis> analysis) {
    String result = null;
    if (!analysis.isEmpty()) {
      result = handle(pod, analysis.get(0));
    }

    return result;
  }

  @Override
  public String handle(Pod pod, RuleAnalysis analysis) {
    final String namespace = pod.getMetadata().getNamespace();
    final String name = pod.getMetadata().getName();
    final String token = java.util.UUID.randomUUID().toString();
    service.acceptToken(token, namespace, name);

    return String.format("%s/api/pods/delete/%s/%s/%s", baseUrl, namespace, name, token);
  }

}
