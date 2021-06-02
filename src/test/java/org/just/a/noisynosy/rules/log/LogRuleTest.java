package org.just.a.noisynosy.rules.log;

import org.junit.Assert;
import org.junit.Test;
import org.just.a.noisynosy.rules.Selector;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import io.fabric8.kubernetes.api.model.Pod;

public class LogRuleTest {

  @Test
  public void should_be_valid() {
    final LogRule rule = new LogRule();

    rule.setName("test");
    rule.setDescription("test description");
    rule.setSelectorsInAnd(givenSelectors(
        givenSelector(true, true), givenSelector(true, false)));
    rule.setMatchesInAnd(givenMatches(givenMatch(true), givenMatch(true)));

    Assert.assertTrue(rule.isValid());
  }

  @Test
  public void should_be_valid_forth() {
    final LogRule rule = new LogRule();

    rule.setName("test");
    rule.setDescription("test description");
    rule.setSelectorsInAnd(givenSelectors(
        givenSelector(true, true), givenSelector(true, false)));
    rule.setMatchesInOr(givenMatches(givenMatch(true), givenMatch(true)));

    Assert.assertTrue(rule.isValid());
  }

  @Test
  public void should_be_valid_second() {
    final LogRule rule = new LogRule();

    rule.setName("test");
    rule.setDescription("test description");
    rule.setSelectorsInOr(givenSelectors(
        givenSelector(true, true), givenSelector(true, false)));
    rule.setMatchesInAnd(givenMatches(givenMatch(true), givenMatch(true)));

    Assert.assertTrue(rule.isValid());
  }

  @Test
  public void should_be_valid_third() {
    final LogRule rule = new LogRule();

    rule.setName("test");
    rule.setDescription("test description");
    rule.setSelectorsInOr(givenSelectors(
        givenSelector(true, true), givenSelector(true, false)));
    rule.setMatchesInOr(givenMatches(givenMatch(true), givenMatch(true)));

    Assert.assertTrue(rule.isValid());
  }

  @Test
  public void should_not_be_valid() {
    final LogRule rule = new LogRule();

    rule.setDescription("test description");
    rule.setSelectorsInAnd(givenSelectors(
        givenSelector(true, true), givenSelector(true, false)));
    rule.setMatchesInAnd(givenMatches(givenMatch(true), givenMatch(true)));

    Assert.assertFalse(rule.isValid());
  }

  @Test
  public void should_not_be_valid_eight() {
    final LogRule rule = new LogRule();

    rule.setName("test");
    rule.setDescription("test description");
    rule.setSelectorsInOr(givenSelectors(
        givenSelector(true, true), givenSelector(true, false)));
    rule.setMatchesInAnd(givenMatches(givenMatch(true), givenMatch(true)));
    rule.setMatchesInOr(givenMatches(givenMatch(true), givenMatch(true)));

    Assert.assertFalse(rule.isValid());
  }

  @Test
  public void should_not_be_valid_fifth() {
    final LogRule rule = new LogRule();

    rule.setName("test");
    rule.setDescription("test description");
    rule.setSelectorsInOr(givenSelectors(
        givenSelector(true, true), givenSelector(true, false)));

    Assert.assertFalse(rule.isValid());
  }

  @Test
  public void should_not_be_valid_for_matches() {
    final LogRule rule = new LogRule();

    rule.setName("test");
    rule.setDescription("test description");
    rule.setSelectorsInOr(givenSelectors(
        givenSelector(true, true), givenSelector(true, false)));
    rule.setMatchesInAnd(givenMatches(givenMatch(false), givenMatch(true)));

    Assert.assertFalse(rule.isValid());
  }

  @Test
  public void should_not_be_valid_for_selectors() {
    final LogRule rule = new LogRule();

    rule.setName("test");
    rule.setDescription("test description");
    rule.setSelectorsInOr(givenSelectors(
        givenSelector(true, true), givenSelector(false, false)));
    rule.setMatchesInAnd(givenMatches(givenMatch(true), givenMatch(true)));

    Assert.assertFalse(rule.isValid());
  }

  @Test
  public void should_not_be_valid_forth() {
    final LogRule rule = new LogRule();

    rule.setName("test");
    rule.setDescription("test description");
    rule.setSelectorsInOr(givenSelectors(
        givenSelector(true, true), givenSelector(true, false)));
    rule.setMatchesInAnd(givenMatches());

    Assert.assertFalse(rule.isValid());
  }

  @Test
  public void should_not_be_valid_second() {
    final LogRule rule = new LogRule();

    rule.setName("test");
    rule.setSelectorsInOr(givenSelectors(
        givenSelector(true, true), givenSelector(true, false)));
    rule.setMatchesInAnd(givenMatches(givenMatch(true), givenMatch(true)));

    Assert.assertFalse(rule.isValid());
  }

  @Test
  public void should_not_be_valid_seventh() {
    final LogRule rule = new LogRule();

    rule.setName("test");
    rule.setDescription("test description");
    rule.setSelectorsInOr(givenSelectors(
        givenSelector(true, true), givenSelector(true, false)));
    rule.setSelectorsInAnd(givenSelectors(
        givenSelector(true, true), givenSelector(true, false)));
    rule.setMatchesInAnd(givenMatches(givenMatch(true), givenMatch(true)));

    Assert.assertFalse(rule.isValid());
  }

  @Test
  public void should_not_be_valid_sixth() {
    final LogRule rule = new LogRule();

    rule.setName("test");
    rule.setDescription("test description");
    rule.setSelectorsInOr(givenSelectors(
        givenSelector(true, true), givenSelector(true, false)));

    Assert.assertFalse(rule.isValid());
  }

  @Test
  public void should_not_be_valid_third() {
    final LogRule rule = new LogRule();

    rule.setName("test");
    rule.setDescription("test description");
    rule.setSelectorsInOr(givenSelectors());
    rule.setMatchesInAnd(givenMatches(givenMatch(true), givenMatch(true)));

    Assert.assertFalse(rule.isValid());
  }

  @Test
  public void should_not_select_pod() {
    final Pod pod = new Pod();

    final LogRule rule = new LogRule();
    rule.setSelectorsInAnd(givenSelectors(
        givenSelector(true, false)));

    Assert.assertFalse(rule.doesItSelect(pod));
  }

  @Test
  public void should_not_select_pod_second() {
    final Pod pod = new Pod();

    final LogRule rule = new LogRule();
    rule.setSelectorsInAnd(givenSelectors(
        givenSelector(true, true),
        givenSelector(true, false)));

    Assert.assertFalse(rule.doesItSelect(pod));
  }

  @Test
  public void should_not_select_pod_third() {
    final Pod pod = new Pod();

    final LogRule rule = new LogRule();
    rule.setSelectorsInOr(givenSelectors(
        givenSelector(true, false),
        givenSelector(true, false)));

    Assert.assertFalse(rule.doesItSelect(pod));
  }

  @Test
  public void should_select_pod() {
    final Pod pod = new Pod();

    final LogRule rule = new LogRule();
    rule.setSelectorsInOr(givenSelectors(
        givenSelector(true, true), givenSelector(true, true)));

    Assert.assertTrue(rule.doesItSelect(pod));
  }

  @Test
  public void should_select_pod_in_or() {
    final Pod pod = new Pod();

    final LogRule rule = new LogRule();
    rule.setSelectorsInOr(givenSelectors(
        givenSelector(true, true),
        givenSelector(true, false)));

    Assert.assertTrue(rule.doesItSelect(pod));
  }

  @Test
  public void should_select_pod_second() {
    final Pod pod = new Pod();

    final LogRule rule = new LogRule();
    rule.setSelectorsInAnd(givenSelectors(
        givenSelector(true, true)));

    Assert.assertTrue(rule.doesItSelect(pod));
  }

  private LogMatch givenMatch(boolean isValid) {
    final LogMatch match = Mockito.mock(LogMatch.class);
    Mockito.when(match.isValid()).thenReturn(isValid);

    return match;
  }

  private List<LogMatch> givenMatches(LogMatch... matches) {
    final List<LogMatch> result = new ArrayList<>();

    for (final LogMatch match : matches) {
      result.add(match);
    }

    return result;
  }

  private Selector givenSelector(boolean isValid, boolean doesSelect) {
    final Selector selector = Mockito.mock(Selector.class);
    Mockito.when(selector.isValid()).thenReturn(isValid);
    Mockito.when(selector.doesItSelect(Mockito.any())).thenReturn(doesSelect);

    return selector;
  }

  private List<Selector> givenSelectors(Selector... selectors) {
    final List<Selector> result = new ArrayList<>();

    for (final Selector selector : selectors) {
      result.add(selector);
    }

    return result;
  }
}
