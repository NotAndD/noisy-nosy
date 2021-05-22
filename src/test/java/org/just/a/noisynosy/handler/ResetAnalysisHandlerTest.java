package org.just.a.noisynosy.handler;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.just.a.noisynosy.analyzer.analysis.RuleAnalysis;
import org.just.a.noisynosy.rules.Rule;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.Pod;

public class ResetAnalysisHandlerTest {

  private ResetAnalysisHandler handler;

  @Before
  public void setup() {
    handler = new ResetAnalysisHandler();
    ReflectionTestUtils.setField(handler, "baseUrl", "test.noisy.io");
  }

  @Test
  public void should_handle_multiple_rules_for_pod() {
    final String result = handler.handle(givenTestPod(), givenRulesAnalysis(3));

    Assert.assertEquals("test.noisy.io/api/pods/reset/test/test-pod-42", result);
  }

  @Test
  public void should_handle_rule_for_pod() {
    final String result = handler.handle(givenTestPod(), givenRuleAnalysis(0));

    Assert.assertEquals("test.noisy.io/api/pods/reset/test/test-pod-42/test-rule-0", result);
  }

  private RuleAnalysis givenRuleAnalysis(int num) {
    final Rule rule = new Rule();
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
