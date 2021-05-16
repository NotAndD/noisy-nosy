package org.just.a.noisynosy.rules;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.fabric8.kubernetes.api.model.Pod;
import lombok.Data;

@Data
public class Rule {

  private static final Logger LOGGER = Logger.getLogger(Rule.class.getName());

  private String name;
  private String description;

  private List<Selector> selectorsInAnd;
  private List<Selector> selectorsInOr;

  private List<Match> matchesInAnd;
  private List<Match> matchesInOr;

  public boolean doesItSelect(Pod pod) {
    return doSelectorsInAndMatches(pod) && doSelectorsInOrMatches(pod);
  }

  public boolean isValid() {
    if (name == null || description == null) {
      LOGGER.log(Level.WARNING, "Rules mus have both name and description");
      return false;
    }

    if (selectorsInAnd != null && selectorsInOr != null) {
      LOGGER.log(Level.WARNING, "Rules cannot have selectors both in AND and OR");
      return false;
    }
    if (matchesInAnd != null && matchesInOr != null) {
      LOGGER.log(Level.WARNING, "Rules cannot have matches both in AND and OR");
      return false;
    }

    return (selectorsInAnd == null || selectorsInAnd.stream().allMatch(Selector::isValid))
        && (selectorsInOr == null || selectorsInOr.stream().allMatch(Selector::isValid))
        && (matchesInAnd == null || matchesInAnd.stream().allMatch(Match::isValid))
        && (matchesInOr == null || matchesInOr.stream().allMatch(Match::isValid));
  }

  private boolean doSelectorsInAndMatches(Pod pod) {
    return selectorsInAnd == null
        || selectorsInAnd.stream().allMatch(s -> s.doesItSelect(pod));
  }

  private boolean doSelectorsInOrMatches(Pod pod) {
    return selectorsInOr == null
        || selectorsInOr.stream().anyMatch(s -> s.doesItSelect(pod));
  }
}
