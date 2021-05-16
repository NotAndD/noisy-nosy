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
