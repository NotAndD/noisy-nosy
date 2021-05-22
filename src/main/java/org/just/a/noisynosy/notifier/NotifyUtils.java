package org.just.a.noisynosy.notifier;

import org.just.a.noisynosy.rules.Rule;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import io.fabric8.kubernetes.api.model.Pod;

public final class NotifyUtils {

  public static final String POD_NAME = "{{pod-name}}";
  public static final String POD_NAMESPACE = "{{pod-namespace}}";

  public static final String RULE_NAME = "{{rule-name}}";
  public static final String RULE_DESCRIPTION = "{{rule-description}}";
  public static final String NOW = "{{now}}";

  public static final String MULTI_RULE_NAME = "{{rule-name-%s}}";
  public static final String MULTI_RULE_DESCRIPTION = "{{rule-description-%s}}";

  public static String buildTemplate(String template, Pod pod, List<Rule> rules,
      Map<String, String> customMappings) {
    String result = template;

    result = result.replace(POD_NAME, pod.getMetadata().getName())
        .replace(POD_NAMESPACE, pod.getMetadata().getNamespace())
        .replace(NOW, new Date().toString());

    for (int i = 0; i < rules.size(); i++) {
      result = result.replace(String.format(MULTI_RULE_NAME, i), rules.get(i).getName())
          .replace(String.format(MULTI_RULE_DESCRIPTION, i), rules.get(i).getDescription());
    }

    for (final Entry<String, String> entry : customMappings.entrySet()) {
      result = result.replace(entry.getKey(), entry.getValue());
    }

    return result;
  }

  public static String buildTemplate(String template, Pod pod, Rule rule,
      Map<String, String> customMappings) {
    String result = template;

    result = result.replace(POD_NAME, pod.getMetadata().getName())
        .replace(POD_NAMESPACE, pod.getMetadata().getNamespace())
        .replace(RULE_NAME, rule.getName())
        .replace(RULE_DESCRIPTION, rule.getDescription())
        .replace(NOW, new Date().toString());

    for (final Entry<String, String> entry : customMappings.entrySet()) {
      result = result.replace(entry.getKey(), entry.getValue());
    }

    return result;
  }

  private NotifyUtils() {
    super();
  }

}
