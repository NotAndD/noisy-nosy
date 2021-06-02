package org.just.a.noisynosy.handler;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.just.a.noisynosy.analyzer.analysis.RuleAnalysis;
import org.just.a.noisynosy.k8s.KubeService;
import org.just.a.noisynosy.rules.log.LogRule;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.Pod;

public class DeletePodHandlerTest {

  private KubeService service;

  private DeletePodHandler handler;

  @Before
  public void setup() {
    service = Mockito.mock(KubeService.class);

    handler = new DeletePodHandler(service);
    ReflectionTestUtils.setField(handler, "baseUrl", "test.noisy.io");
  }

  @Test
  public void should_handle_and_prepare_rule_for_pod() {
    final String result = handler.handle(givenTestPod(), givenRuleAnalysis(0));

    final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
    Mockito.verify(service).acceptToken(captor.capture(), Mockito.eq("test"),
        Mockito.eq("test-pod-42"));
    Assert.assertEquals("test.noisy.io/api/pods/delete/test/test-pod-42/" + captor.getValue(),
        result);
  }

  @Test
  public void should_handle_and_prepare_rules_for_pod() {
    final String result = handler.handle(givenTestPod(), givenRulesAnalysis(4));

    final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
    Mockito.verify(service).acceptToken(captor.capture(), Mockito.eq("test"),
        Mockito.eq("test-pod-42"));
    Assert.assertEquals("test.noisy.io/api/pods/delete/test/test-pod-42/" + captor.getValue(),
        result);
  }

  private RuleAnalysis givenRuleAnalysis(int num) {
    final LogRule rule = new LogRule();
    rule.setName("test-rule-" + num);
    rule.setMatchesInAnd(new ArrayList<>());
    final RuleAnalysis result = new RuleAnalysis(rule);

    return result;
  }

  private List<RuleAnalysis> givenRulesAnalysis(int howMany) {
    final List<RuleAnalysis> result = new ArrayList<>();

    for (int i = 0; i < howMany; i++) {
      result.add(givenRuleAnalysis(i));
    }

    return result;
  }

  private Pod givenTestPod() {
    final Pod result = new Pod();
    result.setMetadata(new ObjectMeta());
    result.getMetadata().setName("test-pod-42");
    result.getMetadata().setNamespace("test");

    return result;
  }

}
