package org.just.a.noisynosy.analyzer.analysis.status;

import org.just.a.noisynosy.analyzer.analysis.MatchAnalysis;
import org.just.a.noisynosy.rules.status.StatusMatch;
import org.just.a.noisynosy.utils.ExplanationUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.fabric8.kubernetes.api.model.ContainerStateTerminated;
import io.fabric8.kubernetes.api.model.ContainerStatus;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodCondition;

public class StatusMatchAnalysis implements MatchAnalysis {

  private static final String PENDING_PHASE = "Pending";
  private static final String ERROR_REASON = "Error";

  private final StatusMatch match;

  private boolean wasSatisfied;
  private final List<String> explanations;

  private final Map<String, Integer> restartNumbers;

  private Instant onPendingFrom;
  private Instant onErrorFrom;

  public StatusMatchAnalysis(StatusMatch match) {
    this.match = match;
    this.restartNumbers = new HashMap<>();
    this.wasSatisfied = false;
    this.explanations = new ArrayList<>();

    this.onPendingFrom = null;
    this.onErrorFrom = null;
  }

  @Override
  public String getSatisfiedExplanation() {
    return String.join("\n", explanations);
  }

  @Override
  public boolean isSatisfied() {
    return wasSatisfied;
  }

  @Override
  public boolean isSatisfiedWith(Object obj) {
    if (obj instanceof Pod == false) {
      return false;
    }

    return isSatisfiedWith((Pod) obj);
  }

  @Override
  public void reset() {
    wasSatisfied = false;
    onPendingFrom = null;
    onErrorFrom = null;

    explanations.clear();
  }

  private boolean anyStatusInError(List<ContainerStatus> statuses) {
    return statuses.stream().anyMatch(status -> status.getState() != null
        && status.getState().getTerminated() != null
        && ERROR_REASON.equals(status.getState().getTerminated().getReason()));
  }

  private boolean anyStatusInError(Pod pod) {
    return anyStatusInError(pod.getStatus().getInitContainerStatuses())
        || anyStatusInError(pod.getStatus().getContainerStatuses());
  }

  private void checkError(Pod pod) {
    final boolean anyStatusInError = anyStatusInError(pod);
    if (anyStatusInError && onErrorFrom == null) {
      onErrorFrom = Instant.now();
    } else if (!anyStatusInError && onErrorFrom != null) {
      onErrorFrom = null;
    }

    if (onErrorFrom != null && (match.getErrorThreshold() == null
        || Instant.now().minusMillis(match.getErrorThreshold())
            .isAfter(onErrorFrom))) {
      explanations.add("Pod is in <Error> state from at least: " + onErrorFrom);
      wasSatisfied = true;
    }
  }

  private void checkPending(Pod pod) {
    final String phase = pod.getStatus().getPhase();
    if (PENDING_PHASE.equals(phase) && onPendingFrom == null) {
      onPendingFrom = Instant.now();
    } else if (!PENDING_PHASE.equals(phase) && onPendingFrom != null) {
      onPendingFrom = null;
    }

    if (onPendingFrom != null && (match.getPendingThreshold() == null
        || Instant.now().minusMillis(match.getPendingThreshold())
            .isAfter(onPendingFrom))) {
      explanations.add("Pod is in <Pending> state from at least: " + onPendingFrom);
      wasSatisfied = true;
    }
  }

  private void checkRestarts(Pod pod) {
    pod.getStatus().getContainerStatuses().forEach(cs -> {
      final String containerKey = getContainerKey(cs);
      final Integer restartCount = cs.getRestartCount();

      if (!restartNumbers.containsKey(containerKey)) {
        restartNumbers.put(containerKey, restartCount);
      } else {
        final Integer oldRestartCount = restartNumbers.get(containerKey);
        if (restartCount > oldRestartCount) {
          explanations.add("Pod <Failed> and was <Restarted>");
          wasSatisfied = true;
          restartNumbers.put(containerKey, restartCount);
        }
      }
    });
  }

  private void explainTerminatedState(ContainerStateTerminated state, String template,
      String containerKey) {
    final String reason = state.getReason();
    final String message = ExplanationUtils.limitTo(state.getMessage(), 1024, true);
    final Integer exitCode = state.getExitCode();

    explanations.add(String.format(template, containerKey, exitCode, reason, message));
  }

  private String getContainerKey(ContainerStatus status) {
    return status.getName() != null
        ? status.getName() : status.getContainerID();
  }

  private boolean isSatisfiedWith(Pod pod) {
    if (Boolean.TRUE.equals(match.getOnRestart())) {
      checkRestarts(pod);
    }
    if (Boolean.TRUE.equals(match.getOnPending())) {
      checkPending(pod);
    }
    if (Boolean.TRUE.equals(match.getOnError())) {
      checkError(pod);
    }

    if (wasSatisfied) {
      preparePodStatusExplanation(pod);
    }

    return wasSatisfied;
  }

  private void prepareConditionsExplanation(List<PodCondition> conditions) {
    final Map<String, List<String>> conditionExplanation = new HashMap<>();
    conditions.stream().forEach(condition -> {
      final String status = condition.getStatus();
      final String type = condition.getType();

      if (!conditionExplanation.containsKey(status)) {
        conditionExplanation.put(status, new ArrayList<>());
      }
      conditionExplanation.get(status).add(type);
    });

    explanations.add("Conditions:");
    conditionExplanation.entrySet().forEach(entry -> explanations
        .add(String.format("  %s: %s", entry.getKey(), entry.getValue())));
  }

  private void prepareContainerStatusExplanation(List<ContainerStatus> statuses) {
    statuses.forEach(status -> {
      final String containerKey = getContainerKey(status);
      if (status.getState().getWaiting() != null) {
        final String reason = status.getState().getWaiting().getReason();
        explanations.add(String.format("  <%s> is <Waiting> for Reason: %s",
            containerKey, reason));
      } else if (status.getState().getTerminated() != null) {
        explainTerminatedState(status.getState().getTerminated(),
            "  <%s> is <Terminated> with Exit Code <%s> for Reason: %s, with Message:%n%s",
            containerKey);
      }

      if (status.getLastState().getTerminated() != null) {
        explainTerminatedState(status.getLastState().getTerminated(),
            "  <%s> was <Terminated> with Exit Code <%s> for Reason: %s, with Message:%n%s",
            containerKey);
      }
    });
  }

  private void preparePodStatusExplanation(Pod pod) {
    prepareConditionsExplanation(pod.getStatus().getConditions());

    final List<ContainerStatus> initContainerStatuses = pod.getStatus()
        .getInitContainerStatuses();
    if (initContainerStatuses != null && !initContainerStatuses.isEmpty()) {
      explanations.add("Init Containers:");
      prepareContainerStatusExplanation(initContainerStatuses);
    }
    final List<ContainerStatus> containerStatuses = pod.getStatus()
        .getContainerStatuses();
    if (containerStatuses != null && !containerStatuses.isEmpty()) {
      explanations.add("Containers:");
      prepareContainerStatusExplanation(containerStatuses);
    }
  }

}
