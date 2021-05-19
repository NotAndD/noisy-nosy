package org.just.a.noisynosy.utils;

import org.just.a.noisynosy.rules.Match;
import org.just.a.noisynosy.rules.Rule;

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
