package org.just.a.noisynosy.rules.status;

import org.just.a.noisynosy.analyzer.analysis.MatchAnalysis;
import org.just.a.noisynosy.analyzer.analysis.status.StatusMatchAnalysis;
import org.just.a.noisynosy.rules.Match;

import java.util.logging.Level;
import java.util.logging.Logger;

import lombok.Data;

@Data
public class StatusMatch implements Match {

  private static final Logger LOGGER = Logger.getLogger(StatusMatch.class.getName());

  private Boolean onRestart;
  private Boolean onPending;
  private Boolean onError;

  private Integer pendingThreshold;
  private Integer errorThreshold;

  public boolean isValid() {
    if (!Boolean.TRUE.equals(onRestart) || !Boolean.TRUE.equals(onPending)
        || !Boolean.TRUE.equals(onError)) {
      LOGGER.log(Level.WARNING, "Empty match is not allowed, discarded.");
      return false;
    }

    return true;
  }

  @Override
  public MatchAnalysis startAnalysis() {
    return new StatusMatchAnalysis(this);
  }

}
