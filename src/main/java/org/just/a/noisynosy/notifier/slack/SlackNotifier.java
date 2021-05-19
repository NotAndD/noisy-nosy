package org.just.a.noisynosy.notifier.slack;

import org.just.a.noisynosy.analyzer.analysis.RuleAnalysis;
import org.just.a.noisynosy.notifier.Notifier;
import org.just.a.noisynosy.notifier.NotifyUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.fabric8.kubernetes.api.model.Pod;

@Component
@ConditionalOnProperty(
    value = "notifiers.slack.enabled",
    havingValue = "true")
public class SlackNotifier extends Notifier {

  private static final Logger LOGGER = Logger.getLogger(SlackNotifier.class.getName());

  private final RestTemplate template;

  @Value("${notifiers.slack.url}")
  private String apiUrl;

  @Value("${notifiers.slack.template}")
  private String messageTemplate;

  public SlackNotifier() {
    this.template = new RestTemplate();
  }

  @Override
  public void notify(Pod pod, RuleAnalysis analysis, Map<String, String> handlerActions) {
    final String message = NotifyUtils.buildTemplate(messageTemplate, pod,
        analysis.getRule(), handlerActions);
    final SlackRequest request = new SlackRequest(message);

    LOGGER.log(Level.INFO,
        () -> String.format("Forwarding a Slack message for %s/%s to slack API url",
            pod.getMetadata().getNamespace(), pod.getMetadata().getName()));
    template.postForLocation(apiUrl, request);
  }
}
