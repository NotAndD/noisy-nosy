package org.just.a.noisynosy.k8s;

import org.junit.Before;
import org.junit.Test;
import org.just.a.noisynosy.analyzer.PodAnalyzer;
import org.just.a.noisynosy.analyzer.analysis.RuleAnalysis;
import org.just.a.noisynosy.handler.Handler;
import org.just.a.noisynosy.notifier.Notifier;
import org.just.a.noisynosy.rules.WatchFor;
import org.just.a.noisynosy.utils.RuleMockUtils;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.Pod;

public class KubeWatcherTest {

  private WatchFor config;

  private KubeClient client;

  private Handler handler;
  private Notifier notifier;

  private KubeWatcher watcher;

  @Before
  public void setup() {
    client = Mockito.mock(KubeClient.class);
    handler = Mockito.mock(Handler.class);
    notifier = Mockito.mock(Notifier.class);
    config = givenTestConfig();

    final List<Handler> handlers = new ArrayList<>();
    handlers.add(handler);
    final List<Notifier> notifiers = new ArrayList<>();
    notifiers.add(notifier);
    watcher = new KubeWatcher(config, handlers, notifiers, client);
  }

  @Test
  public void should_close_analyzer() throws IOException {
    Mockito.when(client.getPods()).thenReturn(givenTestPods());
    final Map<String, PodAnalyzer> watches = new HashMap<>();
    final PodAnalyzer analyzer = Mockito.mock(PodAnalyzer.class);
    watches.put("zzz_zzz-pod-42", analyzer);
    ReflectionTestUtils.setField(watcher, "watches", watches);

    watcher.setup();

    Mockito.verify(analyzer).close();
  }

  @Test
  public void should_close_analyzer_second() throws IOException {
    Mockito.when(client.getPods()).thenReturn(givenTestPods());
    watcher.setup();

    final Map<String, PodAnalyzer> watches = new HashMap<>();
    final PodAnalyzer analyzer = Mockito.mock(PodAnalyzer.class);
    watches.put("zzz_zzz-pod-42", analyzer);
    ReflectionTestUtils.setField(watcher, "watches", watches);

    watcher.updateWatches();

    Mockito.verify(analyzer).close();
  }

  @Test
  public void should_close_the_analyzer() throws IOException {
    final Map<String, PodAnalyzer> watches = new HashMap<>();
    final PodAnalyzer analyzer = Mockito.mock(PodAnalyzer.class);
    watches.put("zzz_zzz-pod-42", analyzer);
    ReflectionTestUtils.setField(watcher, "watches", watches);

    watcher.close();

    Mockito.verify(client).close();
    Mockito.verify(analyzer).close();
  }

  @Test
  public void should_close_the_client() throws IOException {
    watcher.close();

    Mockito.verify(client).close();
  }

  @Test
  public void should_do_nothing_on_no_results_when_checking() {
    final Map<String, PodAnalyzer> watches = new HashMap<>();
    final PodAnalyzer analyzer = Mockito.mock(PodAnalyzer.class);
    watches.put("zzz_zzz-pod-42", analyzer);
    ReflectionTestUtils.setField(watcher, "watches", watches);
    Mockito.when(analyzer.checkResults()).thenReturn(new ArrayList<>());

    watcher.peekALook();

    Mockito.verify(analyzer).checkResults();
    Mockito.verify(handler, Mockito.never()).handle(Mockito.any(), Mockito.anyList());
    Mockito.verify(notifier, Mockito.never()).notify(Mockito.any(), Mockito.anyList(),
        Mockito.any());
  }

  @Test
  public void should_handle_and_notify_when_checking_with_results() {
    final Map<String, PodAnalyzer> watches = new HashMap<>();
    final PodAnalyzer analyzer = Mockito.mock(PodAnalyzer.class);
    watches.put("zzz_zzz-pod-42", analyzer);
    ReflectionTestUtils.setField(watcher, "watches", watches);
    final RuleAnalysis analysis = Mockito.mock(RuleAnalysis.class);
    final List<RuleAnalysis> analyzerResult = new ArrayList<>();
    analyzerResult.add(analysis);
    Mockito.when(analyzer.checkResults()).thenReturn(analyzerResult);

    watcher.peekALook();

    Mockito.verify(analyzer).checkResults();
    Mockito.verify(handler).handle(Mockito.any(), Mockito.anyList());
    Mockito.verify(notifier).notify(Mockito.any(), Mockito.anyList(), Mockito.any());
  }

  @Test
  public void should_not_reset_analysis() {
    final Map<String, PodAnalyzer> watches = new HashMap<>();
    final PodAnalyzer analyzer = Mockito.mock(PodAnalyzer.class);
    watches.put("zzz_zzz-pod-42", analyzer);
    ReflectionTestUtils.setField(watcher, "watches", watches);

    watcher.resetAnalysis("test", "zzz-pod-42", "special-rule");

    Mockito.verify(analyzer, Mockito.never()).resetAnalysis("special-rule");
  }

  @Test
  public void should_not_select_pod_more_than_one_time() {
    Mockito.when(client.getPods()).thenReturn(givenTestPods());
    watcher.setup();

    watcher.updateWatches();
    watcher.updateWatches();

    Mockito.verify(client, Mockito.times(3)).getPods();
    Mockito.verify(client, Mockito.times(1)).watchPodLogs(Mockito.eq("test"),
        Mockito.eq("test-pod-42"), Mockito.any());
    Mockito.verify(client, Mockito.times(1)).watchPodLogs(Mockito.eq("test"),
        Mockito.eq("test-pod-333"), Mockito.any());
    Mockito.verify(client, Mockito.times(1)).watchPodLogs(Mockito.eq("aaa"),
        Mockito.eq("aaa-pod-11"), Mockito.any());
    Mockito.verify(client, Mockito.never()).watchPodLogs(Mockito.eq("ccc"),
        Mockito.eq("ccc-pod-111"), Mockito.any());
    Mockito.verify(client, Mockito.never()).watchPodLogs(Mockito.eq("ccc"),
        Mockito.eq("ccc-pod-222"), Mockito.any());
  }

  @Test
  public void should_reset_analysis() {
    final Map<String, PodAnalyzer> watches = new HashMap<>();
    final PodAnalyzer analyzer = Mockito.mock(PodAnalyzer.class);
    watches.put("zzz_zzz-pod-42", analyzer);
    ReflectionTestUtils.setField(watcher, "watches", watches);

    watcher.resetAnalysis("zzz", "zzz-pod-42", "special-rule");

    Mockito.verify(analyzer).resetAnalysis("special-rule");
  }

  @Test
  public void should_setup() {
    watcher.setup();

    Mockito.verify(client).getPods();
  }

  @Test
  public void should_start_analysis() {
    Mockito.when(client.getPods()).thenReturn(givenTestPods());
    watcher.setup();

    Mockito.verify(client).getPods();
    Mockito.verify(client).watchPodLogs(Mockito.eq("test"), Mockito.eq("test-pod-42"),
        Mockito.any());
    Mockito.verify(client).watchPodLogs(Mockito.eq("test"), Mockito.eq("test-pod-333"),
        Mockito.any());
    Mockito.verify(client).watchPodLogs(Mockito.eq("aaa"), Mockito.eq("aaa-pod-11"),
        Mockito.any());
    Mockito.verify(client, Mockito.never()).watchPodLogs(Mockito.eq("ccc"),
        Mockito.eq("ccc-pod-111"), Mockito.any());
    Mockito.verify(client, Mockito.never()).watchPodLogs(Mockito.eq("ccc"),
        Mockito.eq("ccc-pod-222"), Mockito.any());
  }

  @Test
  public void should_update_watches_with_new_ones() {
    Mockito.when(client.getPods())
        .thenReturn(givenTestPods()).thenReturn(givenOtherTestPods());
    watcher.setup();

    watcher.updateWatches();
    watcher.updateWatches();

    Mockito.verify(client, Mockito.times(3)).getPods();
    Mockito.verify(client, Mockito.times(1)).watchPodLogs(Mockito.eq("test"),
        Mockito.eq("test-pod-42"), Mockito.any());
    Mockito.verify(client, Mockito.times(1)).watchPodLogs(Mockito.eq("test"),
        Mockito.eq("test-pod-333"), Mockito.any());
    Mockito.verify(client, Mockito.times(1)).watchPodLogs(Mockito.eq("aaa"),
        Mockito.eq("aaa-pod-11"), Mockito.any());
    Mockito.verify(client, Mockito.never()).watchPodLogs(Mockito.eq("ccc"),
        Mockito.eq("ccc-pod-111"), Mockito.any());
    Mockito.verify(client, Mockito.never()).watchPodLogs(Mockito.eq("ccc"),
        Mockito.eq("ccc-pod-222"), Mockito.any());
    Mockito.verify(client, Mockito.times(1)).watchPodLogs(Mockito.eq("bbb"),
        Mockito.eq("bbb-pod-222"), Mockito.any());
  }

  private List<Pod> givenOtherTestPods() {
    final List<Pod> result = new ArrayList<>();
    result.add(givenPod("test", "test-pod-42"));
    result.add(givenPod("aaa", "aaa-pod-11"));
    result.add(givenPod("ccc", "ccc-pod-111"));
    result.add(givenPod("ccc", "ccc-pod-222"));
    result.add(givenPod("bbb", "bbb-pod-222"));

    return result;
  }

  private Pod givenPod(String namespace, String name) {
    final Pod pod = new Pod();
    pod.setMetadata(new ObjectMeta());
    pod.getMetadata().setName(name);
    pod.getMetadata().setNamespace(namespace);

    return pod;
  }

  private WatchFor givenTestConfig() {
    final WatchFor result = new WatchFor();
    result.setLogRules(new ArrayList<>());

    result.getLogRules().add(RuleMockUtils.givenRuleInAnd(
        RuleMockUtils.givenSelectorNamespaces("test")));
    result.getLogRules().add(RuleMockUtils.givenRuleInAnd(
        RuleMockUtils.givenSelectorNamespaces("aaa")));
    result.getLogRules().add(RuleMockUtils.givenRuleInAnd(
        RuleMockUtils.givenSelectorNamespaces("bbb")));

    return result;
  }

  private List<Pod> givenTestPods() {
    final List<Pod> result = new ArrayList<>();
    result.add(givenPod("test", "test-pod-42"));
    result.add(givenPod("test", "test-pod-333"));
    result.add(givenPod("aaa", "aaa-pod-11"));
    result.add(givenPod("ccc", "ccc-pod-111"));
    result.add(givenPod("ccc", "ccc-pod-222"));

    return result;
  }

}
