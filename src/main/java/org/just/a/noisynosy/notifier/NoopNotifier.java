package org.just.a.noisynosy.notifier;

import org.just.a.noisynosy.analyzer.analysis.RuleAnalysis;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import io.fabric8.kubernetes.api.model.Pod;

@Component
@ConditionalOnProperty(
    value = "notifiers.noop.enabled",
    havingValue = "true")
public class NoopNotifier implements Notifier {

  private static final Logger LOGGER = Logger.getLogger(NoopNotifier.class.getName());

  @Value("${notifiers.noop.single-template}")
  private String singleTemplate;

  @Value("${notifiers.noop.multiple-template}")
  private String multipleTemplate;

  @Override
  public void notify(Pod pod, List<RuleAnalysis> analysis,
      Map<String, String> handlerActions) {
    if (analysis.size() == 1) {
      notify(pod, analysis.get(0), handlerActions);
    } else {
      LOGGER.log(Level.INFO, () -> NotifyUtils.buildTemplate(multipleTemplate, pod,
          analysis.stream().map(a -> a.getRule()).collect(Collectors.toList()),
          handlerActions));
    }
  }

  @Override
  public void notify(Pod pod, RuleAnalysis analysis, Map<String, String> handlerActions) {
    LOGGER.log(Level.INFO, () -> NotifyUtils.buildTemplate(singleTemplate, pod,
        analysis.getRule(), handlerActions));
  }

}
