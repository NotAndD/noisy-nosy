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

  public boolean isValid() {
    if (valuesInAnd == null && valuesInOr == null) {
      LOGGER.log(Level.WARNING, "Empty match is not allowed.");
      return false;
    }
    if (howMany == null || howMany < 1) {
      LOGGER.log(Level.WARNING, "Empty match is not allowed.");
      howMany = 1;
    }

    return true;
  }
}
