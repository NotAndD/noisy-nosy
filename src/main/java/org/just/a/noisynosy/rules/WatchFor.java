package org.just.a.noisynosy.rules;

import org.just.a.noisynosy.rules.log.LogRule;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

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

  private boolean validityChecked = false;

  public void checkValidity() {
    if (validityChecked) {
      return;
    }
    LOGGER.log(Level.INFO, "Validating watch configuration..");

    if (logRules != null) {
      logRules = logRules.stream()
          .filter(Rule::isValid)
          .collect(Collectors.toList());
    }
    validityChecked = true;
    LOGGER.log(Level.INFO, "Validating watch configuration.. done");
  }

}
