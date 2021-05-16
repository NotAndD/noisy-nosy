package org.just.a.noisynosy.analyzer.analysis;

import org.just.a.noisynosy.rules.Rule;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Data;

@Data
public class RuleAnalysis {

  private List<MatchAnalysis> matchAnalysis;
  private Rule rule;

  private boolean isSatisfied = false;

  public RuleAnalysis(Rule rule) {
    this.rule = rule;
    matchAnalysis = new ArrayList<>();

    if (rule.getMatchesInAnd() != null) {
      matchAnalysis = rule.getMatchesInAnd().stream().map(MatchAnalysis::new)
          .collect(Collectors.toList());
    } else {
      matchAnalysis = rule.getMatchesInOr().stream().map(MatchAnalysis::new)
          .collect(Collectors.toList());
    }
  }

  public boolean isSatisfied() {
    if (isSatisfied) {
      return true;
    }

    if (rule.getMatchesInAnd() != null) {
      isSatisfied = matchAnalysis.stream().allMatch(MatchAnalysis::isSatisfied);
    } else {
      isSatisfied = matchAnalysis.stream().anyMatch(MatchAnalysis::isSatisfied);
    }
    return isSatisfied;
  }

  public boolean isSatisfiedWith(String line) {
    matchAnalysis.forEach(a -> a.isSatisfiedWith(line));

    return isSatisfied();
  }

  public boolean wasSatisfied() {
    return isSatisfied;
  }

}
