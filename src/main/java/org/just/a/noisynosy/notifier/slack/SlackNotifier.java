package org.just.a.noisynosy.notifier.slack;

import org.just.a.noisynosy.analyzer.analysis.RuleAnalysis;
import org.just.a.noisynosy.notifier.Notifier;
import org.just.a.noisynosy.notifier.NotifyUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import io.fabric8.kubernetes.api.model.Pod;

@Component
@ConditionalOnProperty(
    value = "notifiers.slack.enabled",
    havingValue = "true")
public class SlackNotifier implements Notifier {

  private static final Logger LOGGER = Logger.getLogger(SlackNotifier.class.getName());

  private final RestTemplate template;

  @Value("${notifiers.slack.url}")
  private String apiUrl;

  @Value("${notifiers.slack.single-template}")
  private String singleTemplate;

  @Value("${notifiers.slack.multiple-template}")
  private String multipleTemplate;

  public SlackNotifier() {
    this.template = new RestTemplate();
  }

  @Override
  public void notify(Pod pod, List<RuleAnalysis> analysis,
      Map<String, String> handlerActions) {
    if (analysis.size() == 1) {
      notify(pod, analysis.get(0), handlerActions);
    } else {
      final String message = NotifyUtils.buildTemplate(multipleTemplate, pod,
          analysis.stream().map(a -> a.getRule()).collect(Collectors.toList()),
          handlerActions);
      sendSlackMessage(message, pod.getMetadata().getNamespace(), pod.getMetadata().getName());
    }
  }

  @Override
  public void notify(Pod pod, RuleAnalysis analysis, Map<String, String> handlerActions) {
    final String message = NotifyUtils.buildTemplate(singleTemplate, pod,
        analysis.getRule(), handlerActions);
    sendSlackMessage(message, pod.getMetadata().getNamespace(), pod.getMetadata().getName());
  }

  private void sendSlackMessage(String message, String name, String namespace) {
    final SlackRequest request = new SlackRequest(message);

    LOGGER.log(Level.INFO,
        () -> String.format("Forwarding a Slack message for %s/%s to slack API url",
            namespace, name));
    template.postForLocation(apiUrl, request);
  }
}
