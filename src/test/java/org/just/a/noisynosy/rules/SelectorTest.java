package org.just.a.noisynosy.rules;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.Pod;

public class SelectorTest {

  @Test
  public void should_be_valid_first() {
    final Selector selector = new Selector();
    selector.setLabelsMatch(new HashMap<>());
    selector.getLabelsMatch().put("aaa", "bbb");

    Assert.assertTrue(selector.isValid());
  }

  @Test
  public void should_be_valid_second() {
    final Selector selector = new Selector();
    selector.setNamespaceMatch(new ArrayList<>());
    selector.getNamespaceMatch().add("test");

    Assert.assertTrue(selector.isValid());
  }

  @Test
  public void should_be_valid_third() {
    final Selector selector = new Selector();
    selector.setNamespaceMatch(new ArrayList<>());
    selector.getNamespaceMatch().add("test");

    Assert.assertTrue(selector.isValid());
  }

  @Test
  public void should_not_be_valid_first() {
    final Selector selector = new Selector();

    Assert.assertFalse(selector.isValid());
  }

  @Test
  public void should_not_be_valid_second() {
    final Selector selector = new Selector();
    selector.setNamespaceMatch(new ArrayList<>());
    selector.setLabelsMatch(new HashMap<>());

    Assert.assertFalse(selector.isValid());
  }

  @Test
  public void should_not_be_valid_third() {
    final Selector selector = new Selector();
    selector.setNamespaceMatch(new ArrayList<>());
    selector.getNamespaceMatch().add("test");
    selector.setLabelsMatch(new HashMap<>());

    Assert.assertFalse(selector.isValid());
  }

  @Test
  public void should_not_select_pod_by_labels() {
    final Selector selector = new Selector();
    selector.setLabelsMatch(new HashMap<>());
    selector.getLabelsMatch().put("aaa", "bbb");

    final Map<String, String> podLabales = new HashMap<>();
    podLabales.put("ccc", "bbb");
    Assert.assertFalse(selector.doesItSelect(givenPod("test", "test-aaa-123", podLabales)));
  }

  @Test
  public void should_not_select_pod_by_multiple_things_first() {
    final Selector selector = new Selector();
    selector.setLabelsMatch(new HashMap<>());
    selector.getLabelsMatch().put("aaa", "bbb");
    selector.setPodStartsWith(new ArrayList<>());
    selector.getPodStartsWith().add("test-aaa-");
    selector.setNamespaceMatch(new ArrayList<>());
    selector.getNamespaceMatch().add("test");

    final Map<String, String> podLabales = new HashMap<>();
    podLabales.put("ccc", "bbb");
    podLabales.put("ddd", "bbb");
    Assert.assertFalse(selector.doesItSelect(givenPod("test", "test-aaa-123", podLabales)));
  }

  @Test
  public void should_not_select_pod_by_name() {
    final Selector selector = new Selector();
    selector.setPodStartsWith(new ArrayList<>());
    selector.getPodStartsWith().add("test-aaa-");

    Assert.assertFalse(selector.doesItSelect(givenPod("ppp", "ppp-aaa-123", null)));
  }

  @Test
  public void should_not_select_pod_by_namespace() {
    final Selector selector = new Selector();
    selector.setNamespaceMatch(new ArrayList<>());
    selector.getNamespaceMatch().add("test");

    Assert.assertFalse(selector.doesItSelect(givenPod("ppp", "ppp-aaa-123", null)));
  }

  @Test
  public void should_select_pod_by_label() {
    final Selector selector = new Selector();
    selector.setLabelsMatch(new HashMap<>());
    selector.getLabelsMatch().put("aaa", "bbb");

    final Map<String, String> podLabales = new HashMap<>();
    podLabales.put("aaa", "bbb");
    Assert.assertTrue(selector.doesItSelect(givenPod("test", "test-aaa-123", podLabales)));
  }

  @Test
  public void should_select_pod_by_multiple_things_first() {
    final Selector selector = new Selector();
    selector.setLabelsMatch(new HashMap<>());
    selector.getLabelsMatch().put("aaa", "bbb");
    selector.setPodStartsWith(new ArrayList<>());
    selector.getPodStartsWith().add("test-aaa-");

    final Map<String, String> podLabales = new HashMap<>();
    podLabales.put("ccc", "bbb");
    podLabales.put("aaa", "bbb");
    Assert.assertTrue(selector.doesItSelect(givenPod("test", "test-aaa-123", podLabales)));
  }

  @Test
  public void should_select_pod_by_multiple_things_second() {
    final Selector selector = new Selector();
    selector.setLabelsMatch(new HashMap<>());
    selector.getLabelsMatch().put("aaa", "bbb");
    selector.setPodStartsWith(new ArrayList<>());
    selector.getPodStartsWith().add("test-aaa-");
    selector.setNamespaceMatch(new ArrayList<>());
    selector.getNamespaceMatch().add("test");

    final Map<String, String> podLabales = new HashMap<>();
    podLabales.put("ccc", "bbb");
    podLabales.put("aaa", "bbb");
    Assert.assertTrue(selector.doesItSelect(givenPod("test", "test-aaa-123", podLabales)));
  }

  @Test
  public void should_select_pod_by_name() {
    final Selector selector = new Selector();
    selector.setPodStartsWith(new ArrayList<>());
    selector.getPodStartsWith().add("test-aaa-");

    Assert.assertTrue(selector.doesItSelect(givenPod("test", "test-aaa-123", null)));
  }

  @Test
  public void should_select_pod_by_namespace() {
    final Selector selector = new Selector();
    selector.setNamespaceMatch(new ArrayList<>());
    selector.getNamespaceMatch().add("test");

    Assert.assertTrue(selector.doesItSelect(givenPod("test", "test-aaa-123", null)));
  }

  private Pod givenPod(String namespace, String name, Map<String, String> labels) {
    final Pod pod = new Pod();
    pod.setMetadata(new ObjectMeta());
    pod.getMetadata().setName(name);
    pod.getMetadata().setNamespace(namespace);
    pod.getMetadata().setLabels(labels);

    return pod;
  }

}
