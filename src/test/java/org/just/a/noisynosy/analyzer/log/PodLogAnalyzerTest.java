package org.just.a.noisynosy.analyzer.log;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.just.a.noisynosy.analyzer.analysis.RuleAnalysis;
import org.just.a.noisynosy.k8s.KubeClient;
import org.just.a.noisynosy.rules.Rule;
import org.just.a.noisynosy.utils.RuleMockUtils;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.Pod;

public class PodLogAnalyzerTest {

  private KubeClient client;

  private Pod pod;

  private PodLogAnalyzer analyzer;

  @Before
  public void setup() {
    client = Mockito.mock(KubeClient.class);

    pod = new Pod();
    pod.setMetadata(new ObjectMeta());
    pod.getMetadata().setNamespace("test_namespace");
    pod.getMetadata().setName("test_name");
  }

  @Test
  public void should_find_some_results() throws Exception {
    final List<Rule> rules = new ArrayList<>();
    rules.add(RuleMockUtils.givenLogRuleInAnd(
        RuleMockUtils.givenLogMatchInAnd(3, "sometimes")));
    final String shortLog = "Welcome!\n"
        + "sometimes things work\n"
        + "and sometimes they doesn't\n"
        + "\n"
        + "(most of the times)\n"
        + "but sometimes, just sometimes..\n"
        + "things are perfect.";

    analyzer = new PodLogAnalyzer(client, pod, rules);
    analyzer.beginAnalysis();
    final ByteArrayOutputStream logStream =
        (ByteArrayOutputStream) ReflectionTestUtils.getField(analyzer, "logStream");

    logStream.write(shortLog.getBytes());

    final List<RuleAnalysis> results = analyzer.checkResults();

    Assert.assertTrue(results.size() == 1);
  }

  @Test
  public void should_find_some_results_at_second_check() throws Exception {
    final List<Rule> rules = new ArrayList<>();
    rules.add(RuleMockUtils.givenLogRuleInAnd(
        RuleMockUtils.givenLogMatchInAnd(1, "Exception", "caused by")));
    final String firstPiece = "Welcome!\n"
        + "sometimes things work\n"
        + "and sometimes they doesn't\n";
    final String secondPiece = "Exception! caused by\n"
        + "someone\n"
        + "someone else\n"
        + "\n"
        + "(most of the times)\n"
        + "but sometimes, just sometimes..\n"
        + "things are perfect.";

    analyzer = new PodLogAnalyzer(client, pod, rules);
    analyzer.beginAnalysis();

    final ByteArrayOutputStream logStream =
        (ByteArrayOutputStream) ReflectionTestUtils.getField(analyzer, "logStream");

    logStream.write(firstPiece.getBytes());
    final List<RuleAnalysis> firstResults = analyzer.checkResults();

    logStream.write(secondPiece.getBytes());
    final List<RuleAnalysis> secondResults = analyzer.checkResults();

    Assert.assertTrue(firstResults.isEmpty());
    Assert.assertTrue(secondResults.size() == 1);
  }

  @Test
  public void should_not_read_logs_more_than_one_time() throws Exception {
    final List<Rule> rules = new ArrayList<>();
    rules.add(RuleMockUtils.givenLogRuleInAnd(
        RuleMockUtils.givenLogMatchInAnd(3, "sometimes")));
    final String firstPiece = "Welcome!\n"
        + "sometimes things work\n"
        + "and sometimes they doesn't\n";

    analyzer = new PodLogAnalyzer(client, pod, rules);
    analyzer.beginAnalysis();

    final ByteArrayOutputStream logStream =
        (ByteArrayOutputStream) ReflectionTestUtils.getField(analyzer, "logStream");

    logStream.write(firstPiece.getBytes());
    final List<RuleAnalysis> firstResults = analyzer.checkResults();
    final List<RuleAnalysis> secondResults = analyzer.checkResults();

    Assert.assertTrue(firstResults.isEmpty());
    Assert.assertTrue(secondResults.isEmpty());
  }

  @Test
  public void should_start_analysis() {
    final List<Rule> rules = new ArrayList<>();
    rules.add(RuleMockUtils.givenLogRuleInAnd(
        RuleMockUtils.givenLogMatchInAnd(3, "sometimes")));

    analyzer = new PodLogAnalyzer(client, pod, rules);
    analyzer.beginAnalysis();

    Mockito.verify(client, Mockito.times(1))
        .watchPodLogs(Mockito.eq("test_namespace"), Mockito.eq("test_name"), Mockito.any());
  }

}
