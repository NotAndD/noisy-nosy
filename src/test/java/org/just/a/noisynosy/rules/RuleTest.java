package org.just.a.noisynosy.rules;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import io.fabric8.kubernetes.api.model.Pod;

public class RuleTest {

  @Test
  public void should_be_valid() {
    final Rule rule = new Rule();

    rule.setName("test");
    rule.setDescription("test description");
    rule.setSelectorsInAnd(givenSelectors(
        givenSelector(true, true), givenSelector(true, false)));
    rule.setMatchesInAnd(givenMatches(givenMatch(true), givenMatch(true)));

    Assert.assertTrue(rule.isValid());
  }

  @Test
  public void should_be_valid_forth() {
    final Rule rule = new Rule();

    rule.setName("test");
    rule.setDescription("test description");
    rule.setSelectorsInAnd(givenSelectors(
        givenSelector(true, true), givenSelector(true, false)));
    rule.setMatchesInOr(givenMatches(givenMatch(true), givenMatch(true)));

    Assert.assertTrue(rule.isValid());
  }

  @Test
  public void should_be_valid_second() {
    final Rule rule = new Rule();

    rule.setName("test");
    rule.setDescription("test description");
    rule.setSelectorsInOr(givenSelectors(
        givenSelector(true, true), givenSelector(true, false)));
    rule.setMatchesInAnd(givenMatches(givenMatch(true), givenMatch(true)));

    Assert.assertTrue(rule.isValid());
  }

  @Test
  public void should_be_valid_third() {
    final Rule rule = new Rule();

    rule.setName("test");
    rule.setDescription("test description");
    rule.setSelectorsInOr(givenSelectors(
        givenSelector(true, true), givenSelector(true, false)));
    rule.setMatchesInOr(givenMatches(givenMatch(true), givenMatch(true)));

    Assert.assertTrue(rule.isValid());
  }

  @Test
  public void should_not_be_valid() {
    final Rule rule = new Rule();

    rule.setDescription("test description");
    rule.setSelectorsInAnd(givenSelectors(
        givenSelector(true, true), givenSelector(true, false)));
    rule.setMatchesInAnd(givenMatches(givenMatch(true), givenMatch(true)));

    Assert.assertFalse(rule.isValid());
  }

  @Test
  public void should_not_be_valid_eight() {
    final Rule rule = new Rule();

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
    final Rule rule = new Rule();

    rule.setName("test");
    rule.setDescription("test description");
    rule.setSelectorsInOr(givenSelectors(
        givenSelector(true, true), givenSelector(true, false)));

    Assert.assertFalse(rule.isValid());
  }

  @Test
  public void should_not_be_valid_for_matches() {
    final Rule rule = new Rule();

    rule.setName("test");
    rule.setDescription("test description");
    rule.setSelectorsInOr(givenSelectors(
        givenSelector(true, true), givenSelector(true, false)));
    rule.setMatchesInAnd(givenMatches(givenMatch(false), givenMatch(true)));

    Assert.assertFalse(rule.isValid());
  }

  @Test
  public void should_not_be_valid_for_selectors() {
    final Rule rule = new Rule();

    rule.setName("test");
    rule.setDescription("test description");
    rule.setSelectorsInOr(givenSelectors(
        givenSelector(true, true), givenSelector(false, false)));
    rule.setMatchesInAnd(givenMatches(givenMatch(true), givenMatch(true)));

    Assert.assertFalse(rule.isValid());
  }

  @Test
  public void should_not_be_valid_forth() {
    final Rule rule = new Rule();

    rule.setName("test");
    rule.setDescription("test description");
    rule.setSelectorsInOr(givenSelectors(
        givenSelector(true, true), givenSelector(true, false)));
    rule.setMatchesInAnd(givenMatches());

    Assert.assertFalse(rule.isValid());
  }

  @Test
  public void should_not_be_valid_second() {
    final Rule rule = new Rule();

    rule.setName("test");
    rule.setSelectorsInOr(givenSelectors(
        givenSelector(true, true), givenSelector(true, false)));
    rule.setMatchesInAnd(givenMatches(givenMatch(true), givenMatch(true)));

    Assert.assertFalse(rule.isValid());
  }

  @Test
  public void should_not_be_valid_seventh() {
    final Rule rule = new Rule();

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
    final Rule rule = new Rule();

    rule.setName("test");
    rule.setDescription("test description");
    rule.setSelectorsInOr(givenSelectors(
        givenSelector(true, true), givenSelector(true, false)));

    Assert.assertFalse(rule.isValid());
  }

  @Test
  public void should_not_be_valid_third() {
    final Rule rule = new Rule();

    rule.setName("test");
    rule.setDescription("test description");
    rule.setSelectorsInOr(givenSelectors());
    rule.setMatchesInAnd(givenMatches(givenMatch(true), givenMatch(true)));

    Assert.assertFalse(rule.isValid());
  }

  @Test
  public void should_not_select_pod() {
    final Pod pod = new Pod();

    final Rule rule = new Rule();
    rule.setSelectorsInAnd(givenSelectors(
        givenSelector(true, false)));

    Assert.assertFalse(rule.doesItSelect(pod));
  }

  @Test
  public void should_not_select_pod_second() {
    final Pod pod = new Pod();

    final Rule rule = new Rule();
    rule.setSelectorsInAnd(givenSelectors(
        givenSelector(true, true),
        givenSelector(true, false)));

    Assert.assertFalse(rule.doesItSelect(pod));
  }

  @Test
  public void should_not_select_pod_third() {
    final Pod pod = new Pod();

    final Rule rule = new Rule();
    rule.setSelectorsInOr(givenSelectors(
        givenSelector(true, false),
        givenSelector(true, false)));

    Assert.assertFalse(rule.doesItSelect(pod));
  }

  @Test
  public void should_select_pod() {
    final Pod pod = new Pod();

    final Rule rule = new Rule();
    rule.setSelectorsInOr(givenSelectors(
        givenSelector(true, true), givenSelector(true, true)));

    Assert.assertTrue(rule.doesItSelect(pod));
  }

  @Test
  public void should_select_pod_in_or() {
    final Pod pod = new Pod();

    final Rule rule = new Rule();
    rule.setSelectorsInOr(givenSelectors(
        givenSelector(true, true),
        givenSelector(true, false)));

    Assert.assertTrue(rule.doesItSelect(pod));
  }

  @Test
  public void should_select_pod_second() {
    final Pod pod = new Pod();

    final Rule rule = new Rule();
    rule.setSelectorsInAnd(givenSelectors(
        givenSelector(true, true)));

    Assert.assertTrue(rule.doesItSelect(pod));
  }

  private Match givenMatch(boolean isValid) {
    final Match match = Mockito.mock(Match.class);
    Mockito.when(match.isValid()).thenReturn(isValid);

    return match;
  }

  private List<Match> givenMatches(Match... matches) {
    final List<Match> result = new ArrayList<>();

    for (final Match match : matches) {
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
