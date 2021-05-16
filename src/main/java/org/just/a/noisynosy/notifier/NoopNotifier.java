package org.just.a.noisynosy.notifier;

import org.just.a.noisynosy.analyzer.analysis.RuleAnalysis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.fabric8.kubernetes.api.model.Pod;

@Component
public class NoopNotifier implements Notifier {

  private static final Logger LOGGER = Logger.getLogger(NoopNotifier.class.getName());

  private final NotifierProperties properties;

  @Value("${notifiers.noop.enabled}")
  private Boolean enabled;

  public NoopNotifier(NotifierProperties properties) {
    this.properties = properties;
  }

  @Override
  public void notify(Pod pod, List<RuleAnalysis> analysis) {
    analysis.forEach(a -> notify(pod, a));
  }

  @Override
  public void notify(Pod pod, RuleAnalysis analysis) {
    LOGGER.log(Level.INFO,
        () -> NotifyUtils.buildTemplate(properties.getTemplate(), pod, analysis.getRule()));
  }

}
