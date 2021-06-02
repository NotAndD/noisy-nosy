package org.just.a.noisynosy.rules;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.fabric8.kubernetes.api.model.Pod;
import lombok.Data;

@Data
public abstract class Rule {

  private static final Logger LOGGER = Logger.getLogger(Rule.class.getName());

  private String name;
  private String description;

  private List<Selector> selectorsInAnd;
  private List<Selector> selectorsInOr;

  public abstract boolean areMatchesInAnd();

  public boolean doesItSelect(Pod pod) {
    return doSelectorsInAndMatches(pod) && doSelectorsInOrMatches(pod);
  }

  public abstract List<Match> getMatches();

  public boolean isValid() {
    LOGGER.log(Level.INFO,
        () -> "Checking validity of rule " + (name == null ? "<null name>" : name));
    if (name == null || description == null) {
      LOGGER.log(Level.WARNING, "Rules must have both name and description, discarded.");
      return false;
    }

    return areSelectorsValid() && areMatchesValid();
  }

  public abstract RuleType ruleType();

  protected abstract boolean areMatchesValid();

  private boolean areSelectorsValid() {
    if (selectorsInAnd != null && selectorsInOr != null) {
      LOGGER.log(Level.WARNING, "Rules cannot have selectors both in AND and OR, discarded.");
      return false;
    }
    if (selectorsInAnd == null && selectorsInOr == null
        || selectorsInAnd != null && selectorsInAnd.isEmpty()
        || selectorsInOr != null && selectorsInOr.isEmpty()) {
      LOGGER.log(Level.WARNING, "Rules without selectors are not allowed, discarded.");
      return false;
    }

    return (selectorsInAnd == null || selectorsInAnd.stream().allMatch(Selector::isValid))
        && (selectorsInOr == null || selectorsInOr.stream().allMatch(Selector::isValid));
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
