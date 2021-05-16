/*
 *  * Copyright 2021 Thales Italia spa.  *   * This program is not yet licensed
 * and this file may not be used under any  * circumstance.  
 */

package org.just.a.noisynosy.rules;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "watchfor")
public class WatchFor {

  private List<Rule> logRules;

  private boolean validityChecked = false;

  public void checkValidity() {
    if (validityChecked) {
      return;
    }

    if (logRules != null) {
      logRules = logRules.stream()
          .filter(rule -> !rule.isValid()).collect(Collectors.toList());
    }
    validityChecked = true;
  }

}
