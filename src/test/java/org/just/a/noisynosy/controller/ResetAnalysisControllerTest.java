package org.just.a.noisynosy.controller;

import org.junit.Before;
import org.junit.Test;
import org.just.a.noisynosy.k8s.KubeWatcher;
import org.mockito.Mockito;

public class ResetAnalysisControllerTest {

  private KubeWatcher watcher;

  private ResetAnalysisController controller;

  @Before
  public void setup() {
    watcher = Mockito.mock(KubeWatcher.class);

    controller = new ResetAnalysisController(watcher);
  }

  @Test
  public void should_reset_single_analysis() {
    controller.resetRuleInPod("test", "test-pod-42", "special-rule");

    Mockito.verify(watcher).resetAnalysis("test", "test-pod-42", "special-rule");
  }

  @Test
  public void should_reset_whole_pod() {
    controller.resetWholePod("test", "test-pod-42");

    Mockito.verify(watcher).resetAnalysis("test", "test-pod-42", null);
  }

}
