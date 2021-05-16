package org.just.a.noisynosy.handler;

import org.just.a.noisynosy.analyzer.analysis.RuleAnalysis;
import org.just.a.noisynosy.k8s.KubeService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

import io.fabric8.kubernetes.api.model.Pod;

@Component
@ConditionalOnProperty(
    value = "handlers.delete-pod.enabled",
    havingValue = "true")
public class DeletePodHandler implements Handler {

  private final KubeService service;

  public DeletePodHandler(KubeService service) {
    this.service = service;
  }

  @Override
  public void handle(Pod pod, List<RuleAnalysis> analysis) {
    analysis.forEach(a -> handle(pod, a));
  }

  @Override
  public void handle(Pod pod, RuleAnalysis analysis) {
    service.acceptToken(java.util.UUID.randomUUID().toString(),
        pod.getMetadata().getNamespace(), pod.getMetadata().getName());
  }

}
