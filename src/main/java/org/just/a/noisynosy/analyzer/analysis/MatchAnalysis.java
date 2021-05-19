package org.just.a.noisynosy.analyzer.analysis;

import org.just.a.noisynosy.rules.Match;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Data;

@Data
public class MatchAnalysis {

  private Match match;

  private List<Date> matchDates;

  public MatchAnalysis(Match match) {
    this.match = match;
    this.matchDates = new ArrayList<>();
  }

  public boolean isSatisfied() {
    return matchDates.size() >= match.getHowMany();
  }

  public boolean isSatisfiedWith(String line) {
    final Date now = new Date();
    matchDates = matchDates.stream()
        .filter(d -> d.getTime() >= now.getTime() - match.getHowMuch())
        .collect(Collectors.toList());

    if (match.getValuesInAnd() != null && match.getValuesInAnd()
        .stream().allMatch(p -> line.contains(p))
        || match.getValuesInOr() != null && match.getValuesInOr()
            .stream().anyMatch(p -> line.contains(p))) {
      matchDates.add(now);
    }

    return isSatisfied();
  }

  public void reset() {
    matchDates = new ArrayList<>();
  }

}
