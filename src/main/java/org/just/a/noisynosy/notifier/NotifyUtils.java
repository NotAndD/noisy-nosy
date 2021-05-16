package org.just.a.noisynosy.notifier;

import org.just.a.noisynosy.rules.Rule;

import java.util.Date;

import io.fabric8.kubernetes.api.model.Pod;

public final class NotifyUtils {

  public static final String POD_NAME = "{{pod-name}}";
  public static final String POD_NAMESPACE = "{{pod-namespace}}";

  public static final String RULE_NAME = "{{rule-name}}";
  public static final String RULE_DESCRIPTION = "{{rule-description}}";
  public static final String NOW = "{{now}}";

  public static String buildTemplate(String template, Pod pod, Rule rule) {
    String result = template;

    result = result.replace(POD_NAME, pod.getMetadata().getName())
        .replace(POD_NAMESPACE, pod.getMetadata().getNamespace())
        .replace(RULE_NAME, rule.getName())
        .replace(RULE_DESCRIPTION, rule.getDescription())
        .replace(NOW, new Date().toString());

    return result;
  }

  private NotifyUtils() {
    super();
  }

}
