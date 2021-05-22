package org.just.a.noisynosy.controller;

import org.just.a.noisynosy.k8s.KubeUtils;
import org.just.a.noisynosy.k8s.KubeWatcher;
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
    value = "handlers.reset-analysis.enabled",
    havingValue = "true")
public class ResetAnalysisController {

  private final KubeWatcher watcher;

  public ResetAnalysisController(KubeWatcher watcher) {
    this.watcher = watcher;
  }

  @GetMapping(value = "/pods/reset/{namespace}/{name}/{ruleName}")
  @ResponseBody
  public ResponseEntity<String> resetRuleInPod(@PathVariable String namespace,
      @PathVariable String name, @PathVariable String ruleName) {
    watcher.resetAnalysis(namespace, name, ruleName);

    return new ResponseEntity<>(KubeUtils.getPodKey(namespace, name), HttpStatus.OK);
  }

  @GetMapping(value = "/pods/reset/{namespace}/{name}")
  @ResponseBody
  public ResponseEntity<String> resetWholePod(@PathVariable String namespace,
      @PathVariable String name) {
    watcher.resetAnalysis(namespace, name, null);

    return new ResponseEntity<>(KubeUtils.getPodKey(namespace, name), HttpStatus.OK);
  }

}
