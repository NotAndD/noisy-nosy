package org.just.a.noisynosy.rules.log;

import org.just.a.noisynosy.rules.Match;
import org.just.a.noisynosy.rules.Rule;
import org.just.a.noisynosy.rules.RuleType;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class LogRule extends Rule {

  private static final Logger LOGGER = Logger.getLogger(LogRule.class.getName());

  private List<LogMatch> matchesInAnd;
  private List<LogMatch> matchesInOr;

  @Override
  public boolean areMatchesInAnd() {
    return matchesInAnd != null;
  }

  @Override
  public List<Match> getMatches() {
    return new ArrayList<>(matchesInAnd != null ? matchesInAnd : matchesInOr);
  }

  @Override
  public RuleType ruleType() {
    return RuleType.LOG;
  }

  @Override
  protected boolean areMatchesValid() {
    if (matchesInAnd != null && matchesInOr != null) {
      LOGGER.log(Level.WARNING, "Rules cannot have matches both in AND and OR, discarded.");
      return false;
    }
    if (matchesInAnd == null && matchesInOr == null
        || matchesInAnd != null && matchesInAnd.isEmpty()
        || matchesInOr != null && matchesInOr.isEmpty()) {
      LOGGER.log(Level.WARNING, "Rules without matches are not allowed, discarded.");
      return false;
    }

    return (matchesInAnd == null || matchesInAnd.stream().allMatch(LogMatch::isValid))
        && (matchesInOr == null || matchesInOr.stream().allMatch(LogMatch::isValid));
  }

}
