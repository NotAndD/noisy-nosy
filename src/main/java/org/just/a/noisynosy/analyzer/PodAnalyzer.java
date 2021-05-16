package org.just.a.noisynosy.analyzer;

import org.just.a.noisynosy.analyzer.analysis.RuleAnalysis;
import org.just.a.noisynosy.k8s.KubeClient;
import org.just.a.noisynosy.rules.Rule;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.dsl.LogWatch;
import lombok.Data;

@Data
public class PodAnalyzer implements Closeable {

  private final KubeClient client;
  private final Pod pod;
  private final List<RuleAnalysis> analysis;
  private ByteArrayOutputStream logStream;
  private LogWatch watch;

  public PodAnalyzer(KubeClient client, Pod pod, List<Rule> rules) {
    this.pod = pod;
    this.client = client;
    this.analysis = rules.stream()
        .map(RuleAnalysis::new).collect(Collectors.toList());
  }

  public void beginAnalysis() {
    setup();

    watch = client.watchPodLogs(pod.getMetadata().getNamespace(),
        pod.getMetadata().getName(), logStream);
  }

  public List<RuleAnalysis> checkResults() {
    final List<RuleAnalysis> result = new ArrayList<>();

    final String logs = logStream.toString();
    logStream.reset();

    try (Scanner scanner = new Scanner(logs)) {
      while (scanner.hasNextLine()) {
        final String line = scanner.nextLine();
        final List<RuleAnalysis> newlySatisfiedRules = analysis.stream()
            .filter(a -> !a.wasSatisfied())
            .filter(a -> a.isSatisfiedWith(line))
            .collect(Collectors.toList());

        if (!newlySatisfiedRules.isEmpty()) {
          result.addAll(newlySatisfiedRules);
        }
      }
    }

    return result;
  }

  @Override
  public void close() throws IOException {
    if (watch != null) {
      watch.close();
    }

    if (logStream != null) {
      logStream.close();
    }
  }

  private void setup() {
    if (logStream == null) {
      logStream = new ByteArrayOutputStream();
    }
  }

}
