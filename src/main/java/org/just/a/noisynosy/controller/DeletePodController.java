package org.just.a.noisynosy.controller;

import org.just.a.noisynosy.k8s.KubeService;
import org.just.a.noisynosy.k8s.KubeUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/api")
@ConditionalOnProperty(
    value = "handlers.delete-pod.enabled",
    havingValue = "true")
public class DeletePodController {

  private final KubeService service;

  public DeletePodController(KubeService service) {
    this.service = service;
  }

  @GetMapping(value = "/pods/delete/{namespace}/{name}/{token}")
  @ResponseBody
  public ResponseEntity<String> deletePod(@PathVariable String namespace,
      @PathVariable String name, @PathVariable String token) {
    service.deletePod(token, namespace, name);

    return new ResponseEntity<>(KubeUtils.getPodKey(namespace, name), HttpStatus.OK);
  }

}
