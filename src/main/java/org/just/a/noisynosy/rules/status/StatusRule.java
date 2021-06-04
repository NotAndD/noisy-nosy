package org.just.a.noisynosy.rules.status;

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
public class StatusRule extends Rule {

  private static final Logger LOGGER = Logger.getLogger(StatusRule.class.getName());

  private List<StatusMatch> matchesInOr;

  @Override
  public boolean areMatchesInAnd() {
    return false;
  }

  @Override
  public List<Match> getMatches() {
    return new ArrayList<>(matchesInOr);
  }

  @Override
  public RuleType ruleType() {
    return RuleType.STATUS;
  }

  @Override
  protected boolean areMatchesValid() {
    if (matchesInOr == null
        || matchesInOr != null && matchesInOr.isEmpty()) {
      LOGGER.log(Level.WARNING, "Rules without matches are not allowed, discarded.");
      return false;
    }

    return matchesInOr.stream().allMatch(StatusMatch::isValid);
  }

}
