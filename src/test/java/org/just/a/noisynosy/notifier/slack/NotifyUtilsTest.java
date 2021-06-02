package org.just.a.noisynosy.notifier.slack;

import org.junit.Assert;
import org.junit.Test;
import org.just.a.noisynosy.analyzer.analysis.MatchAnalysis;
import org.just.a.noisynosy.analyzer.analysis.RuleAnalysis;
import org.just.a.noisynosy.notifier.NotifyUtils;
import org.just.a.noisynosy.rules.log.LogRule;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.Pod;

public class NotifyUtilsTest {

  @Test
  public void should_build_template() {
    final String result =
        NotifyUtils.buildTemplate("example template with aaa and {{pod-name}}",
            givenPod(), givenRule(4), givenMappings());

    Assert.assertEquals("example template with bbb and test-pod-42", result);
  }

  @Test
  public void should_build_template_fifth() {
    final RuleAnalysis analysis = givenRule(1);
    final MatchAnalysis match = Mockito.mock(MatchAnalysis.class);
    analysis.getMatchAnalysis().add(match);
    Mockito.when(match.getSatisfiedExplanation())
        .thenReturn("abcd");
    Mockito.when(match.isSatisfied()).thenReturn(true);
    final String result =
        NotifyUtils.buildTemplate("example template with {{satisfied-explanation}}",
            givenPod(), analysis, givenMappings());

    Assert.assertEquals("example template with abcd", result);
  }

  @Test
  public void should_build_template_forth() {
    final String result =
        NotifyUtils.buildTemplate(
            "example template with {{rule-description-10}} and {{pod-namespace}}",
            givenPod(), givenRules(4), givenMappings());

    Assert.assertEquals("example template with {{rule-description-10}} and test", result);
  }

  @Test
  public void should_build_template_second() {
    final String result =
        NotifyUtils.buildTemplate(
            "example template with {{rule-description}} and {{pod-namespace}}",
            givenPod(), givenRule(4), givenMappings());

    Assert.assertEquals("example template with test-description-4 and test", result);
  }

  @Test
  public void should_build_template_third() {
    final String result =
        NotifyUtils.buildTemplate(
            "example template with {{rule-description-2}} and {{pod-namespace}}",
            givenPod(), givenRules(4), givenMappings());

    Assert.assertEquals("example template with test-description-2 and test", result);
  }

  private Map<String, String> givenMappings() {
    final Map<String, String> result = new HashMap<>();
    result.put("aaa", "bbb");
    result.put("ccc", "ddd");

    return result;
  }

  private Pod givenPod() {
    final Pod result = new Pod();
    result.setMetadata(new ObjectMeta());
    result.getMetadata().setNamespace("test");
    result.getMetadata().setName("test-pod-42");

    return result;
  }

  private RuleAnalysis givenRule(int num) {
    final LogRule rule = new LogRule();
    rule.setMatchesInAnd(new ArrayList<>());
    rule.setName("test-rule-" + num);
    rule.setDescription("test-description-" + num);

    return new RuleAnalysis(rule);
  }

  private List<RuleAnalysis> givenRules(int howMany) {
    final List<RuleAnalysis> result = new ArrayList<>();

    for (int i = 0; i < howMany; i++) {
      result.add(givenRule(i));
    }

    return result;
  }
}
