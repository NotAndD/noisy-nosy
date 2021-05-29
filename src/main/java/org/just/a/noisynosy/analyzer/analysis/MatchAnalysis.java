package org.just.a.noisynosy.analyzer.analysis;

import org.just.a.noisynosy.analyzer.analysis.log.MatchingLog;
import org.just.a.noisynosy.rules.Match;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Data;

@Data
public class MatchAnalysis {

  private Match match;

  private List<MatchingLog> matches;

  public MatchAnalysis(Match match) {
    this.match = match;
    this.matches = new ArrayList<>();
  }

  public String getSatisfiedExplanation() {
    return String.join("\n", matches.stream()
        .map(m -> m.getLine()).collect(Collectors.toList()));
  }

  public boolean isSatisfied() {
    return matches.size() >= match.getHowMany();
  }

  public boolean isSatisfiedWith(String line) {
    final Date now = new Date();
    if (match.getHowMuch() != null) {
      matches = matches.stream()
          .filter(m -> m.isAfter(now.getTime() - match.getHowMuch()))
          .collect(Collectors.toList());
    }

    if (match.getValuesInAnd() != null && match.getValuesInAnd()
        .stream().allMatch(p -> line.contains(p))
        || match.getValuesInOr() != null && match.getValuesInOr()
            .stream().anyMatch(p -> line.contains(p))) {
      matches.add(new MatchingLog(line, now));
    }

    return isSatisfied();
  }

  public void reset() {
    matches = new ArrayList<>();
  }

}
