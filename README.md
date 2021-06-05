# noisy-nosy

Just someone who likes to watch your stuff and be noisy about it.

Seriously! This Java Spring boot app likes to watch your Pods, monitor them up, then start being noisy about them if they begin to match with its rules.

#

## Table of Contents

- [Important Notes](##important-notes)
- [Quickstart](##quickstart)
- [Configuration](##Configuration)
- [Examples of Matches](##example-of-matches)

## Important Notes

This small project is still under development and as such, there's no docker image already built for an easy installation and setups for obvious reasons.

This means that, to try things up, you are required to build the code yourself, then build the docker image (there's a `Dockerfile` in the repo), publish it on your private docker registry and then install it with the provided HELM Chart.

Things will be improved when (if) I'll consider the app enough stable and ready for a first silly-release!

#

## Quickstart

NoisyNosy is a small Spring boot app written in Java (8 at the moment) with the only job of running inside a Kubernetes cluster and monitoring the Pods in search for anomalies.

### Inside a Cluster

- A `Dockerfile` is provided for easy docker image builds, located in this repo under `infrastructure/docker`
- An HELM Chart is provided for an easy installation inside Kubernetes clusters, located in this repo under `infrastructure/helm`.

Once the app is Up & Running inside the cluster, it should find the kube endpoints to attach to Kubernetes APIs on its own. Take into consideration that it requires to at least get, list, watch, delete Pods and access to their logs.

### Outside the Cluster

Since this app is using as Kubernetes Client [fabric8io/kubernetes-client](https://github.com/fabric8io/kubernetes-client), it expects in the environment to find a variable called `KUBECONFIG` which points directly to the kubeconfig file to access the cluster.

#

## Configuration

NoisyNosy expects (a lot) of configuration to be declared into its `application.yml`. the HELM Chart mounts all properties located under `configurations` directly as read-only file for the app (at the expected path of the Docker Image)

Additionally, a secret can be specified under `existingSecret` to mount into the environment of the app the various properties which needs to stay sealed, such as the Slack web Hook.

Let's take a look at the various parts of the configuration.

#

### K8s configs

```yaml
k8s:
  watch:
    # millis to wait between each iteration of the cluster watch
    scheduled: 60000
    # millis to wait between each iteration of the cluster setup
    setup: 600000
```

Basically, the app will take a look at the cluster situation each `scheduled` millis.. and will setup (aka removing deleted Pods, adding new Pods and so on) each `setup` millis.

#

### Handlers

```yaml
handlers:
  delete-pod:
    enabled: true
  reset-analysis:
    enabled: true
```

Handlers can be enabled to add actions to the notifications. For example: if a Pod match with a watched Rule, NoisyNosy will prepare to delete such Pod if `delete-pod` is enabled. This is just to safely decide if such actions are generally enabled or not, since they also needs to be manually inserted into the messages (more below).

If an handler is enabled, NoisyNosy will expose a REST endpoint to perform such action, which can then be used from the notifiers to directly provide a simple way for users to immediately perform actions as they read the message.

At the moment, the only supported actions are the following:
- Deleting the Pod
- Consider the match a False alarm and continue monitoring it up.

#

### Notifiers

```yaml
notifiers:
  noop:
    enabled: false
    single-template: |
      Looks like {{pod-namespace}}/{{pod-name}} {{rule-description}}.
      --- match explanation ---
      {{satisfied-explanation}}
      ---
      I could do the following:
      - delete the pod with {{delete-action}}
      - reset the analysis with {{reset-action}}
    multiple-template: |
      Looks like {{pod-namespace}}/{{pod-name}} {{rule-description-0}}, {{rule-description-1}} and so on.
      --- first match explanation ---
      {{satisfied-explanation-0}}
      ---
      I could do the following:
      - delete the pod with {{delete-action}}
      - reset the analysis with {{reset-action}}
  slack:
    enabled: false
    url: this is a secret
    single-template: |
      Help!
      Looks like `{{pod-namespace}}/{{pod-name}}` {{rule-description}}.
      ```
      {{satisfied-explanation}}
      ```
      Should I do something about it?
      - Yes, <{{delete-action}}|delete> the pod
      - Nah, <{{reset-action}}|false alarm>
    multiple-template: |
      Help!
      Looks like `{{pod-namespace}}/{{pod-name}}` {{rule-description-0}}, {{rule-description-1}} and so on.
      ```
      {{satisfied-explanation-0}}
      ```
      Should I do something about it?
      - Yes, <{{delete-action}}|delete> the pod
      - Nah, <{{reset-action}}|false alarm>
```

This is an example configuration for notifiers. Each notifiers is responsible of signaling each matching Pod to a different output, by building a message from a given template.

The supported notifiers for now are just:
- Noop, which means it just writes to the NoisyNosy own Log
- Slack, which sends a Slack message to the specified web hook

Templates must be defined for each notifier enabled, both single and multiple (multiple is used in the rare case where more than one Rule match at the same iteration)

You can write what you want.. and you can use various placeholders for obtaining the match informations. Supported placeholders are the following:
- `{{pod-namespace}}` - the matching Pod Namespace
- `{{pod-name}}` - the matching Pod name
- `{{rule-description}}` - the description of the rule which matched with the Pod (more below)
- `{{satisfied-explanation}}` - the explanation of the match, as it seen by NoisyNosy
- `{{delete-action}}` - the delete action URL for this match
- `{{reset-action}}` - the reset action URL for this match

When there are multiple matches, rules and satisfied explanations are attached to an index to select one of the matching rules. So, for example, `{{rule-description-0}}, {{rule-description-1}}` will describe both the first and second rules matching with the Pod.

In general, a Pod matching with multiple rules at the same time should be rare (hopefully!)

#

### Rules

```yaml
watchfor:
  log-rules:
  - name: generic-exception-check
    description: is writing Exceptions in its log
    matches-in-or:
    - how-many: 3
      how-much: 120000
      values-in-and:
      - Caused by
    - how-many: 3
      how-much: 120000
      values-in-and:
      - Exception
    selectors-in-and:
    - namespace-match:
      - development
      
  status-rules:
  - name: generic-check-status
    description: has Errors in its Status
    matches-in-or:
    - on-restart: true
      on-error: true
      on-pending: true
      error-threshold: 30000
      pending-threshold: 300000
    selectors-in-and:
    - namespace-match:
      - monitoring
      pod-starts-with:
      - prometheus-
      labels-match:
        app: prometheus
```

Rules describe what NoisyNosy should search, as well as which Pods should be selected and kept monitored. Each rule is composed at least of a list of `matches` and a list of `selectors`, together with its name (which acts as sort-of id) and a description.

Selectors can select Pods from various things:
- Their namespace, if it is inside `namespace-match`
- The initial part of their name, if it starts with what specified in `pod-starts-with`
- Their labels, if they match with what specified in `labels-match`

If an item is not declared, then it is not used.. but take into consideration that an empty selector is not allowed.

Matches are of two types. `log-rules` matches may be in AND or in OR (with different properties to be used) and declares substrings to search inside Pods logs, while `status-rules` matches may be only in OR and declares what should be searched inside the status of monitored Pods.

More in details:
- A Log rule match declares values searched into the logs (either in AND or in OR), together with a numeric threshold (aka how many times values should be found) and an optional time threshold (aka how much time can pass before a match is discarded).
    - Notice that matches are applied to lines of the logs.. so values are searched in each line.
    - Notice that `how-much` should be >= of `k8s.watch.scheduled` to have any sense.

- A Status rule match declares phases searched in the Pod status, such as if the Pod restarted (aka one container crashed) or if the Pod is in pending for too much time, or if the Pod is in error for too much time.
    - Notice that threshold fields (`error-threshold` and `pending-threshold`) should be >= of `k8s.watch.scheduled` to have any sense.

#

## Example of Matches

What if a pod is stuck into container creating from too much time?

Formatted as if written on Slack, it's the following message:

#

Help!

Looks like `monitoring/prometheus-operator-grafana-6f4d57dd6c-r9b5j` has Errors in its Status.
```
Pod is in <Pending> state from at least: 2021-06-05T08:58:01.862Z
Conditions:
  True: [PodScheduled]
  False: [Initialized, Ready, ContainersReady]
Init Containers:
  <init-chown-data> is <Waiting> for Reason: PodInitializing
  <grafana-sc-datasources> is <Waiting> for Reason: PodInitializing
Containers:
  <grafana> is <Waiting> for Reason: PodInitializing
  <grafana-sc-dashboard> is <Waiting> for Reason: PodInitializing
```
Should I do something about it?
- Yes, [delete](#noisy-nosy) the pod
- Nah, [false alarm](#noisy-nosy)

#

And what if a Pod is writing exceptions in its logs?

#

Help!

Looks like `development/clickhouse-0` is writing Exceptions in its log.
```
2021.06.05 08:40:12.743681 [ 174 ] {2926224d-a05b-4b4f-a44a-fd3733809128} <Error> executeQuery: Code: 241, e.displayText() = DB::Exception: Memory limit (for query) exceeded: would use 960.78 MiB (attempt to allocate chunk of 8388656 bytes), maximum: 953.67 MiB: While executing ConvertingAggregatedToChunksTransform (version 20.4.3.16 (official build)) (from 10.244.10.142:38322) (in query: SELECT ....... FORMAT TabSeparatedWithNamesAndTypes;), Stack trace (when copying this message, always include the lines below):
0. Poco::Exception::Exception(std::__1::basic_string<char, std::__1::char_traits<char>, std::__1::allocator<char> > const&, int) @ 0x10418760 in /usr/bin/clickhouse
1. DB::Exception::Exception(std::__1::basic_string<char, std::__1::char_traits<char>, std::__1::allocator<char> > const&, int) @ 0x8fff8ad in /usr/bin/clickhouse
```
Should I do something about it?
- Yes, [delete](#noisy-nosy) the pod
- Nah, [false alarm](#noisy-nosy)
