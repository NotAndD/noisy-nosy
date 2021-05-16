/*
 * Copyright 2021 Thales Italia spa.
 * 
 * This program is not yet licensed and this file may not be used under any
 * circumstance.
 */
package org.just.a.noisynosy.notifier;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "notifiers.props")
public class NotifierProperties {

  private String template;

}
