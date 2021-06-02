package org.just.a.noisynosy.k8s;

import io.fabric8.kubernetes.api.model.Pod;

public final class KubeUtils {

  public static String getPodKey(Pod pod) {
    return getPodKey(pod.getMetadata().getNamespace(), pod.getMetadata().getName());
  }

  public static String getPodKey(String namespace, String name) {
    return namespace + "_" + name;
  }

  public static String getPodUid(Pod pod) {
    return pod.getMetadata().getUid();
  }

  private KubeUtils() {
    super();
  }

}
