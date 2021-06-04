package org.just.a.noisynosy.analyzer.analysis.status;

import org.just.a.noisynosy.analyzer.analysis.MatchAnalysis;
import org.just.a.noisynosy.rules.status.StatusMatch;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.fabric8.kubernetes.api.model.ContainerStatus;
import io.fabric8.kubernetes.api.model.Pod;

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
          wasSatisfied = true;
          restartNumbers.put(containerKey, restartCount);
        }
      }
    });
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

  private void prepareContainerStatusExplanation(List<ContainerStatus> statuses) {
    statuses.forEach(status -> {
      final String containerKey = getContainerKey(status);
      explanations.add("Container: " + containerKey);
      if (status.getState().getWaiting() != null) {
        final String reason = status.getState().getWaiting().getReason();
        explanations.add("Current Status: Waiting, Reason: " + reason);
      } else if (status.getState().getTerminated() != null) {
        final String reason = status.getState().getTerminated().getReason();
        final String message = status.getState().getTerminated().getMessage();
        final Integer exitCode = status.getState().getTerminated().getExitCode();
        explanations.add(String.format(
            "Current Status: Terminated with Exit Code %s, Reason: %s, Message: %s",
            exitCode, reason, message));
      }

      if (status.getLastState().getTerminated() != null) {
        final String reason = status.getLastState().getTerminated().getReason();
        final String message = status.getLastState().getTerminated().getMessage();
        final Integer exitCode = status.getLastState().getTerminated().getExitCode();
        explanations.add(String.format(
            "Previous Status: Terminated with Exit Code %s, Reason: %s, Message: %s",
            exitCode, reason, message));
      }
    });
  }

  private void preparePodStatusExplanation(Pod pod) {
    pod.getStatus().getConditions().forEach(condition -> {
      explanations.add(String.format("Condition: %s, Status: %s",
          condition.getType(), condition.getStatus()));
    });
    final List<ContainerStatus> initContainerStatuses = pod.getStatus()
        .getInitContainerStatuses();
    if (initContainerStatuses != null && !initContainerStatuses.isEmpty()) {
      prepareContainerStatusExplanation(initContainerStatuses);
    }
    final List<ContainerStatus> containerStatuses = pod.getStatus()
        .getContainerStatuses();
    if (containerStatuses != null && !containerStatuses.isEmpty()) {
      prepareContainerStatusExplanation(containerStatuses);
    }
  }

}
