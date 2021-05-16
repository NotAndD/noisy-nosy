/*
 *  * Copyright 2021 Thales Italia spa.  *   * This program is not yet licensed
 * and this file may not be used under any  * circumstance.  
 */
package org.just.a.noisynosy.analyzer.analysis;

import org.just.a.noisynosy.rules.Match;

import lombok.Data;

@Data
public class MatchAnalysis {

  private Match match;

  private int satisfiedBy = 0;

  public MatchAnalysis(Match match) {
    this.match = match;
  }

  public boolean isSatisfied() {
    return satisfiedBy >= match.getHowMany();
  }

  public boolean isSatisfiedWith(String line) {
    if (match.getValuesInAnd() != null && match.getValuesInAnd()
        .stream().allMatch(p -> line.contains(p))
        || match.getValuesInOr() != null && match.getValuesInOr()
            .stream().anyMatch(p -> line.contains(p))) {
      satisfiedBy += 1;
    }

    return isSatisfied();
  }

}
