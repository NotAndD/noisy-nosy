/*
 *  * Copyright 2021 Thales Italia spa.  *   * This program is not yet licensed
 * and this file may not be used under any  * circumstance.  
 */
package org.just.a.noisynosy.k8s;

import io.fabric8.kubernetes.api.model.Pod;

public final class KubeUtils {

  public static String getPodKey(Pod pod) {
    return getPodKey(pod.getMetadata().getNamespace(), pod.getMetadata().getName());
  }

  public static String getPodKey(String namespace, String name) {
    return namespace + "_" + name;
  }

  private KubeUtils() {
    super();
  }

}
