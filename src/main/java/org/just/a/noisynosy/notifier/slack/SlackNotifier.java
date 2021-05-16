package org.just.a.noisynosy.notifier.slack;

import org.just.a.noisynosy.analyzer.analysis.RuleAnalysis;
import org.just.a.noisynosy.notifier.Notifier;
import org.just.a.noisynosy.notifier.NotifyUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

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
  public void notify(Pod pod, RuleAnalysis analysis) {
    final String message =
        NotifyUtils.buildTemplate(messageTemplate, pod, analysis.getRule());
    final SlackRequest request = new SlackRequest(message);

    template.postForLocation(apiUrl, request);
  }
}
