package org.just.a.noisynosy.notifier;

import org.just.a.noisynosy.analyzer.analysis.RuleAnalysis;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.fabric8.kubernetes.api.model.Pod;

@Component
@ConditionalOnProperty(
    value = "notifiers.noop.enabled",
    havingValue = "true")
public class NoopNotifier extends Notifier {

  private static final Logger LOGGER = Logger.getLogger(NoopNotifier.class.getName());

  @Value("${notifiers.noop.template}")
  private String messageTemplate;

  @Override
  public void notify(Pod pod, RuleAnalysis analysis, Map<String, String> handlerActions) {
    LOGGER.log(Level.INFO, () -> NotifyUtils.buildTemplate(messageTemplate,
        pod, analysis.getRule(), handlerActions));
  }

}
