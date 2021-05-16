/*
 *  * Copyright 2021 Thales Italia spa.  *   * This program is not yet licensed
 * and this file may not be used under any  * circumstance.  
 */
package org.just.a.noisynosy.k8s;

import org.springframework.stereotype.Component;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class KubeService implements Closeable {

  private static final Logger LOGGER = Logger.getLogger(KubeService.class.getName());

  private final KubeClient client;

  private final Map<String, String> allowedTokens;

  public KubeService(KubeClient client) {
    this.client = client;
    this.allowedTokens = new HashMap<>();
  }

  public void acceptToken(String token, String namespace, String name) {
    allowedTokens.put(KubeUtils.getPodKey(namespace, name), token);
  }

  @Override
  public void close() throws IOException {
    this.client.close();
  }

  public void deletePod(String token, String namespace, String name) {
    verifyToken(token, namespace, name);

    if (Boolean.TRUE.equals(client.deletePod(namespace, name))) {
      LOGGER.log(Level.INFO,
          () -> String.format("Pod removed correctly %s/%s", namespace, name));
    }
  }

  public void verifyToken(String token, String namespace, String name) {
    if (token == null) {
      LOGGER.log(Level.SEVERE, "An empty token is not allowed.");
      throw new RuntimeException("An empty token is not allowed.");
    }

    final String podKey = KubeUtils.getPodKey(namespace, name);
    if (!allowedTokens.containsKey(podKey) || !token.equals(allowedTokens.get(podKey))) {
      LOGGER.log(Level.SEVERE, "Provided token is not allowed.");
      throw new RuntimeException("Provided token is not allowed.");
    }

    allowedTokens.remove(podKey);
  }

}
