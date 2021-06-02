package org.just.a.noisynosy.analyzer.analysis.log;

import org.just.a.noisynosy.analyzer.analysis.MatchAnalysis;
import org.just.a.noisynosy.rules.log.LogMatch;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class LogMatchAnalysis implements MatchAnalysis {

  private final LogMatch match;

  private List<MatchingLog> matches;

  public LogMatchAnalysis(LogMatch match) {
    this.match = match;
    this.matches = new ArrayList<>();
  }

  @Override
  public String getSatisfiedExplanation() {
    return String.join("\n", matches.stream()
        .map(MatchingLog::getLine).collect(Collectors.toList()));
  }

  @Override
  public boolean isSatisfied() {
    return matches.size() >= match.getHowMany();
  }

  @Override
  public boolean isSatisfiedWith(Object obj) {
    if (obj instanceof String == false) {
      return false;
    }

    return isSatisfiedWith((String) obj);
  }

  @Override
  public void reset() {
    matches = new ArrayList<>();
  }

  private boolean isSatisfiedWith(String line) {
    final Date now = new Date();
    if (match.getHowMuch() != null) {
      matches = matches.stream()
          .filter(m -> m.isAfter(now.getTime() - match.getHowMuch()))
          .collect(Collectors.toList());
    }

    if (match.getValuesInAnd() != null && match.getValuesInAnd()
        .stream().allMatch(line::contains)
        || match.getValuesInOr() != null && match.getValuesInOr()
            .stream().anyMatch(line::contains)) {
      matches.add(new MatchingLog(line, now));
    }

    return isSatisfied();
  }

}
