package org.just.a.noisynosy.rules.log;

import org.just.a.noisynosy.analyzer.analysis.MatchAnalysis;
import org.just.a.noisynosy.analyzer.analysis.log.LogMatchAnalysis;
import org.just.a.noisynosy.rules.Match;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import lombok.Data;

@Data
public class LogMatch implements Match {

  private static final Logger LOGGER = Logger.getLogger(LogMatch.class.getName());

  private List<String> valuesInAnd;
  private List<String> valuesInOr;

  private Integer howMany;
  private Integer howMuch;

  public boolean isValid() {
    if (valuesInAnd == null && valuesInOr == null
        || valuesInAnd != null && valuesInAnd.isEmpty()
        || valuesInOr != null && valuesInOr.isEmpty()) {
      LOGGER.log(Level.WARNING, "Empty match is not allowed, discarded.");
      return false;
    }

    if (howMany == null || howMany < 1) {
      LOGGER.log(Level.WARNING, "Empty match is not allowed, discarded.");
      return false;
    }

    return true;
  }

  @Override
  public MatchAnalysis startAnalysis() {
    return new LogMatchAnalysis(this);
  }
}
