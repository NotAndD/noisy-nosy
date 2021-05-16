/*
 * Copyright 2021 Thales Italia spa.
 * 
 * This program is not yet licensed and this file may not be used under any
 * circumstance.
 */
package org.just.a.noisynosy.utils;

import org.just.a.noisynosy.rules.Match;
import org.just.a.noisynosy.rules.Rule;

import java.util.ArrayList;

public final class RuleMockUtils {

  public static Match givenMatchInAnd(int howMany, String... values) {
    final Match result = new Match();
    result.setHowMany(howMany);
    result.setValuesInAnd(new ArrayList<>());
    for (final String val : values) {
      result.getValuesInAnd().add(val);
    }

    return result;
  }

  public static Rule givenRuleInAnd(Match... matches) {
    final Rule result = new Rule();
    result.setMatchesInAnd(new ArrayList<>());
    for (final Match val : matches) {
      result.getMatchesInAnd().add(val);
    }

    return result;
  }

  private RuleMockUtils() {
    super();
  }

}
