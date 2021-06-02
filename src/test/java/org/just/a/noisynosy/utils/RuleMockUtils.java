package org.just.a.noisynosy.utils;

import org.just.a.noisynosy.rules.Selector;
import org.just.a.noisynosy.rules.log.LogMatch;
import org.just.a.noisynosy.rules.log.LogRule;
import org.mockito.Mockito;

import java.util.ArrayList;

public final class RuleMockUtils {

  public static LogMatch givenLogMatchInAnd(int howMany, String... values) {
    final LogMatch result = new LogMatch();
    result.setHowMany(howMany);
    result.setHowMuch(60000);
    result.setValuesInAnd(new ArrayList<>());
    for (final String val : values) {
      result.getValuesInAnd().add(val);
    }

    return result;
  }

  public static LogMatch givenLogMatchInOr(int howMany, String... values) {
    final LogMatch result = new LogMatch();
    result.setHowMany(howMany);
    result.setHowMuch(60000);
    result.setValuesInOr(new ArrayList<>());
    for (final String val : values) {
      result.getValuesInOr().add(val);
    }

    return result;
  }

  public static LogRule givenLogRuleInAnd(LogMatch... matches) {
    final LogRule result = new LogRule();
    result.setName("test-rule");
    result.setDescription("test-description");
    result.setMatchesInAnd(new ArrayList<>());
    result.setSelectorsInAnd(new ArrayList<>());
    final Selector selector = Mockito.mock(Selector.class);
    Mockito.when(selector.isValid()).thenReturn(true);
    result.getSelectorsInAnd().add(selector);

    for (final LogMatch val : matches) {
      result.getMatchesInAnd().add(val);
    }

    return result;
  }

  public static LogRule givenLogRuleInAnd(Selector... selectors) {
    final LogRule result = new LogRule();
    result.setName("test-rule");
    result.setDescription("test-description");
    result.setSelectorsInAnd(new ArrayList<>());
    result.setMatchesInAnd(new ArrayList<>());
    final LogMatch match = Mockito.mock(LogMatch.class);
    Mockito.when(match.isValid()).thenReturn(true);
    result.getMatchesInAnd().add(match);

    for (final Selector val : selectors) {
      result.getSelectorsInAnd().add(val);
    }

    return result;
  }

  public static LogRule givenLogRuleInOr(LogMatch... matches) {
    final LogRule result = new LogRule();
    result.setName("test-rule");
    result.setDescription("test-description");
    result.setMatchesInOr(new ArrayList<>());
    result.setSelectorsInAnd(new ArrayList<>());
    final Selector selector = Mockito.mock(Selector.class);
    Mockito.when(selector.isValid()).thenReturn(true);
    result.getSelectorsInAnd().add(selector);

    for (final LogMatch val : matches) {
      result.getMatchesInOr().add(val);
    }

    return result;
  }

  public static LogRule givenLogRuleInOr(Selector... selectors) {
    final LogRule result = new LogRule();
    result.setName("test-rule");
    result.setDescription("test-description");
    result.setSelectorsInOr(new ArrayList<>());
    result.setMatchesInAnd(new ArrayList<>());
    final LogMatch match = Mockito.mock(LogMatch.class);
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
