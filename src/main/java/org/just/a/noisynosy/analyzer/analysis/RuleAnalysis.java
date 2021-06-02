package org.just.a.noisynosy.analyzer.analysis;

import org.just.a.noisynosy.rules.Match;
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

    matchAnalysis = rule.getMatches().stream().map(Match::startAnalysis)
        .collect(Collectors.toList());
  }

  public String getSatisfiedExplanation() {
    return String.join("\n\n", matchAnalysis.stream()
        .filter(MatchAnalysis::isSatisfied)
        .map(MatchAnalysis::getSatisfiedExplanation)
        .collect(Collectors.toList()));
  }

  public boolean isSatisfied() {
    if (isSatisfied) {
      return true;
    }

    if (rule.areMatchesInAnd()) {
      isSatisfied = matchAnalysis.stream().allMatch(MatchAnalysis::isSatisfied);
    } else {
      isSatisfied = matchAnalysis.stream().anyMatch(MatchAnalysis::isSatisfied);
    }
    return isSatisfied;
  }

  public boolean isSatisfiedWith(Object obj) {
    matchAnalysis.forEach(a -> a.isSatisfiedWith(obj));

    return isSatisfied();
  }

  public void reset() {
    isSatisfied = false;
    matchAnalysis.forEach(MatchAnalysis::reset);
  }

  public boolean wasSatisfied() {
    return isSatisfied;
  }

}
