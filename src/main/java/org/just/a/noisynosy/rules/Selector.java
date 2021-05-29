package org.just.a.noisynosy.rules;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import io.fabric8.kubernetes.api.model.Pod;
import lombok.Data;

@Data
public class Selector {

  private static final Logger LOGGER = Logger.getLogger(Selector.class.getName());

  private Map<String, String> labelsMatch;
  private List<String> namespaceMatch;
  private List<String> podStartsWith;

  public boolean doesItSelect(Pod pod) {
    return doNamespaceMatches(pod) && doLabelsMatches(pod) && doPodStartsWith(pod);
  }

  public boolean isValid() {
    if (labelsMatch == null && namespaceMatch == null && podStartsWith == null
        || labelsMatch != null && labelsMatch.isEmpty()
        || namespaceMatch != null && namespaceMatch.isEmpty()
        || podStartsWith != null && podStartsWith.isEmpty()) {
      LOGGER.log(Level.WARNING, "Empty selector is not allowed, discarded.");
      return false;
    }

    return true;
  }

  private boolean doLabelsMatches(Pod pod) {
    if (labelsMatch == null) {
      return true;
    }

    final Map<String, String> labels = pod.getMetadata().getLabels();
    return labels != null && labelsMatch.entrySet().stream()
        .allMatch(e -> labels.containsKey(e.getKey())
            && e.getValue().equals(labels.get(e.getKey())));
  }

  private boolean doNamespaceMatches(Pod pod) {
    return namespaceMatch == null
        || namespaceMatch.stream().anyMatch(n -> n.equals(pod.getMetadata().getNamespace()));
  }

  private boolean doPodStartsWith(Pod pod) {
    return podStartsWith == null
        || podStartsWith.stream().anyMatch(s -> pod.getMetadata().getName().startsWith(s));
  }
}
