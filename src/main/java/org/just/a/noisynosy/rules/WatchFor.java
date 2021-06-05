package org.just.a.noisynosy.rules;

import org.just.a.noisynosy.rules.log.LogRule;
import org.just.a.noisynosy.rules.status.StatusRule;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "watchfor")
public class WatchFor {

  private static final Logger LOGGER = Logger.getLogger(WatchFor.class.getName());

  private List<LogRule> logRules;

  private List<StatusRule> statusRules;

  private boolean validityChecked = false;

  public void checkValidity() {
    if (validityChecked) {
      return;
    }

    LOGGER.log(Level.INFO, "Validating watch configuration..");

    checkLogRules();
    checkStatusRules();

    validityChecked = true;
    LOGGER.log(Level.INFO, "Validating watch configuration.. done");
  }

  public List<Rule> getAllRules() {
    final List<Rule> result = new ArrayList<>();

    result.addAll(logRules);
    result.addAll(statusRules);

    return result;
  }

  private void checkLogRules() {
    if (logRules == null) {
      logRules = new ArrayList<>();
    }
    logRules = logRules.stream()
        .filter(Rule::isValid)
        .collect(Collectors.toList());
  }

  private void checkStatusRules() {
    if (statusRules == null) {
      statusRules = new ArrayList<>();
    }
    statusRules = statusRules.stream()
        .filter(Rule::isValid)
        .collect(Collectors.toList());
  }

}
