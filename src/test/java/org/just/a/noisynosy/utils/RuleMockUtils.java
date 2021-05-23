package org.just.a.noisynosy.utils;

import org.just.a.noisynosy.rules.Match;
import org.just.a.noisynosy.rules.Rule;
import org.just.a.noisynosy.rules.Selector;
import org.mockito.Mockito;

import java.util.ArrayList;

public final class RuleMockUtils {

  public static Match givenMatchInAnd(int howMany, String... values) {
    final Match result = new Match();
    result.setHowMany(howMany);
    result.setHowMuch(60000);
    result.setValuesInAnd(new ArrayList<>());
    for (final String val : values) {
      result.getValuesInAnd().add(val);
    }

    return result;
  }

  public static Match givenMatchInOr(int howMany, String... values) {
    final Match result = new Match();
    result.setHowMany(howMany);
    result.setHowMuch(60000);
    result.setValuesInOr(new ArrayList<>());
    for (final String val : values) {
      result.getValuesInOr().add(val);
    }

    return result;
  }

  public static Rule givenRuleInAnd(Match... matches) {
    final Rule result = new Rule();
    result.setName("test-rule");
    result.setDescription("test-description");
    result.setMatchesInAnd(new ArrayList<>());
    result.setSelectorsInAnd(new ArrayList<>());
    final Selector selector = Mockito.mock(Selector.class);
    Mockito.when(selector.isValid()).thenReturn(true);
    result.getSelectorsInAnd().add(selector);

    for (final Match val : matches) {
      result.getMatchesInAnd().add(val);
    }

    return result;
  }

  public static Rule givenRuleInAnd(Selector... selectors) {
    final Rule result = new Rule();
    result.setName("test-rule");
    result.setDescription("test-description");
    result.setSelectorsInAnd(new ArrayList<>());
    result.setMatchesInAnd(new ArrayList<>());
    final Match match = Mockito.mock(Match.class);
    Mockito.when(match.isValid()).thenReturn(true);
    result.getMatchesInAnd().add(match);

    for (final Selector val : selectors) {
      result.getSelectorsInAnd().add(val);
    }

    return result;
  }

  public static Rule givenRuleInOr(Match... matches) {
    final Rule result = new Rule();
    result.setName("test-rule");
    result.setDescription("test-description");
    result.setMatchesInOr(new ArrayList<>());
    result.setSelectorsInAnd(new ArrayList<>());
    final Selector selector = Mockito.mock(Selector.class);
    Mockito.when(selector.isValid()).thenReturn(true);
    result.getSelectorsInAnd().add(selector);

    for (final Match val : matches) {
      result.getMatchesInOr().add(val);
    }

    return result;
  }

  public static Rule givenRuleInOr(Selector... selectors) {
    final Rule result = new Rule();
    result.setName("test-rule");
    result.setDescription("test-description");
    result.setSelectorsInOr(new ArrayList<>());
    result.setMatchesInAnd(new ArrayList<>());
    final Match match = Mockito.mock(Match.class);
    Mockito.when(match.isValid()).thenReturn(true);
    result.getMatchesInAnd().add(match);

    for (final Selector val : selectors) {
      result.getSelectorsInOr().add(val);
    }

    return result;
  }

  public static Selector givenSelectorNamespaces(String... values) {
    final Selector result = new Selector();
    result.setNamespaceMatch(new ArrayList<>());
    for (final String val : values) {
      result.getNamespaceMatch().add(val);
    }

    return result;
  }

  private RuleMockUtils() {
    super();
  }

}
