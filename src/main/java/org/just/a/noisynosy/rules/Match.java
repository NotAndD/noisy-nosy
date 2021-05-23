package org.just.a.noisynosy.rules;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import lombok.Data;

@Data
public class Match {

  private static final Logger LOGGER = Logger.getLogger(Match.class.getName());

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
}
