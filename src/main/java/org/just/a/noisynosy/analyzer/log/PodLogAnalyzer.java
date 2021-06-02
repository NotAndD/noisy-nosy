package org.just.a.noisynosy.analyzer.log;

import org.just.a.noisynosy.analyzer.PodAnalyzer;
import org.just.a.noisynosy.k8s.KubeClient;
import org.just.a.noisynosy.rules.Rule;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.dsl.LogWatch;

public class PodLogAnalyzer extends PodAnalyzer {

  private ByteArrayOutputStream logStream;
  private LogWatch watch;

  public PodLogAnalyzer(KubeClient client, Pod pod, List<Rule> rules) {
    super(client, pod, rules);
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

  @Override
  protected void progressAnalysis() {
    final String logs = logStream.toString();
    logStream.reset();

    try (Scanner scanner = new Scanner(logs)) {
      while (scanner.hasNextLine()) {
        final String line = scanner.nextLine();
        getAnalysis().stream()
            .filter(a -> !a.wasSatisfied())
            .forEach(a -> a.isSatisfiedWith(line));
      }
    }
  }

  @Override
  protected void setupAnalysis() {
    if (logStream == null) {
      logStream = new ByteArrayOutputStream();
    }

    watch = getClient().watchPodLogs(getPodNamespace(), getPodName(), logStream);
  }

}
