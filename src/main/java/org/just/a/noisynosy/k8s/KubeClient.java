package org.just.a.noisynosy.k8s;

import org.springframework.stereotype.Component;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.LogWatch;

@Component
public class KubeClient implements Closeable {

  private final KubernetesClient client;

  public KubeClient(KubernetesClient client) {
    this.client = client;
  }

  @Override
  public void close() throws IOException {
    client.close();
  }

  public Boolean deletePod(String namespace, String name) {
    return client.pods().inNamespace(namespace).withName(name).delete();
  }

  public List<Pod> getPods() {
    return client.pods().inAnyNamespace().list().getItems();
  }

  public List<Pod> getPods(String namespace) {
    return client.pods().inNamespace(namespace).list().getItems();
  }

  public LogWatch watchPodLogs(String namespace, String name, OutputStream out) {
    return client.pods().inNamespace(namespace)
        .withName(name).tailingLines(100).watchLog(out);
  }

}
