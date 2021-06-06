package org.just.a.noisynosy.utils;

import org.junit.Assert;
import org.junit.Test;
import org.just.a.noisynosy.analyzer.PodAnalyzer;
import org.just.a.noisynosy.analyzer.log.PodLogAnalyzer;
import org.just.a.noisynosy.analyzer.status.PodStatusAnalyzer;
import org.just.a.noisynosy.k8s.KubeClient;
import org.just.a.noisynosy.rules.Rule;
import org.just.a.noisynosy.rules.log.LogRule;
import org.just.a.noisynosy.rules.status.StatusRule;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import io.fabric8.kubernetes.api.model.Pod;

public class AnalyzerUtilsTest {

  @Test
  public void should_setup_analysis() {
    final KubeClient client = Mockito.mock(KubeClient.class);
    final Pod pod = Mockito.mock(Pod.class);

    final List<PodAnalyzer> result =
        AnalyzerUtils.setupAnalysis(client, pod, givenDifferentRules());

    Assert.assertTrue(result != null && result.size() == 2);
    Assert.assertTrue(result.stream().anyMatch(a -> a instanceof PodLogAnalyzer));
    Assert.assertTrue(result.stream().anyMatch(a -> a instanceof PodStatusAnalyzer));

    final PodAnalyzer logAnalyzer = result.stream()
        .filter(a -> a instanceof PodLogAnalyzer)
        .findFirst().orElse(null);

    Assert.assertTrue(
        logAnalyzer.getAnalysis() != null && logAnalyzer.getAnalysis().size() == 2);

    final PodAnalyzer statusAnalyzer = result.stream()
        .filter(a -> a instanceof PodStatusAnalyzer)
        .findFirst().orElse(null);

    Assert.assertTrue(
        statusAnalyzer.getAnalysis() != null && statusAnalyzer.getAnalysis().size() == 1);
  }

  private List<Rule> givenDifferentRules() {
    final List<Rule> result = new ArrayList<>();

    result.add(givenLogRule());
    result.add(givenStatusRule());
    result.add(givenLogRule());

    return result;
  }

  private LogRule givenLogRule() {
    final LogRule result = new LogRule();

    result.setMatchesInAnd(new ArrayList<>());

    return result;
  }

  private StatusRule givenStatusRule() {
    final StatusRule result = new StatusRule();

    result.setMatchesInOr(new ArrayList<>());

    return result;
  }

}
