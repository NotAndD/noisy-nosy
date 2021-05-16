/*
 *  * Copyright 2021 Thales Italia spa.  *   * This program is not yet licensed
 * and this file may not be used under any  * circumstance.  
 */
package org.just.a.noisynosy.notifier;

import org.just.a.noisynosy.analyzer.analysis.RuleAnalysis;

import java.util.List;

import io.fabric8.kubernetes.api.model.Pod;

public interface Notifier {

  public void notify(Pod pod, List<RuleAnalysis> analysis);

  public void notify(Pod pod, RuleAnalysis analysis);
}
