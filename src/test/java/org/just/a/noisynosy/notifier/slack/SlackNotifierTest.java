package org.just.a.noisynosy.notifier.slack;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.just.a.noisynosy.analyzer.analysis.RuleAnalysis;
import org.just.a.noisynosy.rules.log.LogRule;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.Pod;

public class SlackNotifierTest {

  private RestTemplate template;

  private SlackNotifier notifier;

  @Before
  public void setup() {
    template = Mockito.mock(RestTemplate.class);

    notifier = new SlackNotifier();
    ReflectionTestUtils.setField(notifier, "template", template);
    ReflectionTestUtils.setField(notifier, "singleTemplate", "test-single");
    ReflectionTestUtils.setField(notifier, "multipleTemplate", "test-multiple");
    ReflectionTestUtils.setField(notifier, "apiUrl", "slack.api");
  }

  @Test
  public void should_notify_multiple_template() {
    notifier.notify(givenPod(), givenRuleAnalysis(3), givenActions());

    final ArgumentCaptor<SlackRequest> captor = ArgumentCaptor.forClass(SlackRequest.class);
    Mockito.verify(template).postForLocation(Mockito.eq("slack.api"), captor.capture());

    final SlackRequest request = captor.getValue();
    Assert.assertTrue(request != null);
    Assert.assertEquals("test-multiple", request.getText());
  }

  @Test
  public void should_notify_single_template() {
    notifier.notify(givenPod(), givenAnalysis(1), givenActions());

    final ArgumentCaptor<SlackRequest> captor = ArgumentCaptor.forClass(SlackRequest.class);
    Mockito.verify(template).postForLocation(Mockito.eq("slack.api"), captor.capture());

    final SlackRequest request = captor.getValue();
    Assert.assertTrue(request != null);
    Assert.assertEquals("test-single", request.getText());
  }

  @Test
  public void should_notify_single_template_second() {
    notifier.notify(givenPod(), givenRuleAnalysis(1), givenActions());

    final ArgumentCaptor<SlackRequest> captor = ArgumentCaptor.forClass(SlackRequest.class);
    Mockito.verify(template).postForLocation(Mockito.eq("slack.api"), captor.capture());

    final SlackRequest request = captor.getValue();
    Assert.assertTrue(request != null);
    Assert.assertEquals("test-single", request.getText());
  }

  @Test
  public void should_substitute_text() {
    ReflectionTestUtils.setField(notifier, "singleTemplate",
        "test-single aaa and {{rule-name}}");
    notifier.notify(givenPod(), givenAnalysis(1), givenActions());

    final ArgumentCaptor<SlackRequest> captor = ArgumentCaptor.forClass(SlackRequest.class);
    Mockito.verify(template).postForLocation(Mockito.eq("slack.api"), captor.capture());

    final SlackRequest request = captor.getValue();
    Assert.assertTrue(request != null);
    Assert.assertEquals("test-single bbb and test-rule-1", request.getText());
  }

  @Test
  public void should_substitute_text_second() {
    ReflectionTestUtils.setField(notifier, "multipleTemplate",
        "test-multiple ccc and {{rule-name-0}} {{rule-name-2}}");
    notifier.notify(givenPod(), givenRuleAnalysis(3), givenActions());

    final ArgumentCaptor<SlackRequest> captor = ArgumentCaptor.forClass(SlackRequest.class);
    Mockito.verify(template).postForLocation(Mockito.eq("slack.api"), captor.capture());

    final SlackRequest request = captor.getValue();
    Assert.assertTrue(request != null);
    Assert.assertEquals("test-multiple ddd and test-rule-0 test-rule-2", request.getText());
  }

  private Map<String, String> givenActions() {
    final Map<String, String> result = new HashMap<>();
    result.put("aaa", "bbb");
    result.put("ccc", "ddd");

    return result;
  }

  private RuleAnalysis givenAnalysis(int num) {
    final LogRule rule = new LogRule();
    rule.setMatchesInAnd(new ArrayList<>());
    rule.setName("test-rule-" + num);
    rule.setDescription("test-description-" + num);

    final RuleAnalysis result = new RuleAnalysis(rule);

    return result;
  }

  private Pod givenPod() {
    final Pod result = new Pod();
    result.setMetadata(new ObjectMeta());
    result.getMetadata().setNamespace("test");
    result.getMetadata().setName("test-pod-42");

    return result;
  }

  private List<RuleAnalysis> givenRuleAnalysis(int howMany) {
    final List<RuleAnalysis> result = new ArrayList<>();

    for (int i = 0; i < howMany; i++) {
      result.add(givenAnalysis(i));
    }

    return result;
  }

}
