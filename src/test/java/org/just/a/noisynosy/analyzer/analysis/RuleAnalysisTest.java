/*
 *  * Copyright 2021 Thales Italia spa.  *   * This program is not yet licensed
 * and this file may not be used under any  * circumstance.  
 */
package org.just.a.noisynosy.analyzer.analysis;

import org.just.a.noisynosy.rules.Rule;
import org.just.a.noisynosy.utils.RuleMockUtils;

import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.stream.Collectors;

public class RuleAnalysisTest {

  @Test
  public void rule_should_match_word_sometimes() {
    final String shortLog = "Welcome!\n"
        + "sometimes things work\n"
        + "and sometimes they doesn't\n"
        + "\n"
        + "(most of the times)\n"
        + "but sometimes, just sometimes..\n"
        + "things are perfect.";

    final Rule rule = RuleMockUtils.givenRuleInAnd(
        RuleMockUtils.givenMatchInAnd(3, "sometimes"));
    final RuleAnalysis analysis = new RuleAnalysis(rule);

    makeAnalysisConsumeLog(analysis, shortLog);

    Assert.assertTrue(analysis.isSatisfied());
  }

  @Test
  public void rule_should_match_words_caused_exception() {
    final String shortLog = "Welcome!\n"
        + "sometimes things work\n"
        + "and sometimes they doesn't\n"
        + "Exception! caused by\n"
        + "someone\n"
        + "someone else\n"
        + "\n"
        + "(most of the times)\n"
        + "but sometimes, just sometimes..\n"
        + "things are perfect.";

    final Rule rule = RuleMockUtils.givenRuleInAnd(
        RuleMockUtils.givenMatchInAnd(1, "Exception", "caused by"));
    final RuleAnalysis analysis = new RuleAnalysis(rule);

    makeAnalysisConsumeLog(analysis, shortLog);

    Assert.assertTrue(analysis.isSatisfied());
  }

  @Test
  public void rule_should_match_words_foo_and_bar() {
    final String shortLog = "Uh Uh Uh\n"
        + "I believe that something is about to explode\n"
        + "Let me check.. let's see..\n"
        + "Foo foo foo! Woa\n"
        + "Unexpected, I thought that instead of that\n"
        + "I'd see bar..\n"
        + "whoops";

    final Rule rule = RuleMockUtils.givenRuleInAnd(
        RuleMockUtils.givenMatchInAnd(1, "foo"),
        RuleMockUtils.givenMatchInAnd(1, "bar"));
    final RuleAnalysis analysis = new RuleAnalysis(rule);

    makeAnalysisConsumeLog(analysis, shortLog);

    Assert.assertTrue(analysis.isSatisfied());
  }

  @Test
  public void rule_should_not_match_word_sometimes() {
    final String shortLog = "Welcome!\n"
        + "Sometimes things work\n"
        + "and sometimes they doesn't\n"
        + "\n"
        + "(most of the times)\n"
        + "but sometimes, just sometimes..\n"
        + "things are perfect.";

    final Rule rule = RuleMockUtils.givenRuleInAnd(
        RuleMockUtils.givenMatchInAnd(3, "sometimes"));
    final RuleAnalysis analysis = new RuleAnalysis(rule);

    makeAnalysisConsumeLog(analysis, shortLog);

    Assert.assertFalse(analysis.isSatisfied());
  }

  @Test
  public void should_work_with_long_text_1() {
    final InputStream stream =
        RuleAnalysisTest.class.getResourceAsStream("/log/long-text-1.txt");
    final String longLog = new BufferedReader(new InputStreamReader(stream))
        .lines().collect(Collectors.joining("\n"));

    final Rule rule = RuleMockUtils.givenRuleInAnd(
        RuleMockUtils.givenMatchInAnd(3, "Something"));
    final RuleAnalysis analysis = new RuleAnalysis(rule);

    makeAnalysisConsumeLog(analysis, longLog);

    Assert.assertTrue(analysis.isSatisfied());
  }

  @Test
  public void should_work_with_long_text_2() {
    final InputStream stream =
        RuleAnalysisTest.class.getResourceAsStream("/log/long-text-2.txt");
    final String longLog = new BufferedReader(new InputStreamReader(stream))
        .lines().collect(Collectors.joining("\n"));

    final Rule rule = RuleMockUtils.givenRuleInAnd(
        RuleMockUtils.givenMatchInAnd(1, "caused by", "Exception"),
        RuleMockUtils.givenMatchInAnd(5, "Something"));
    final RuleAnalysis analysis = new RuleAnalysis(rule);

    makeAnalysisConsumeLog(analysis, longLog);

    Assert.assertTrue(analysis.isSatisfied());
  }

  private void makeAnalysisConsumeLog(RuleAnalysis analysis, String log) {
    try (Scanner scanner = new Scanner(log)) {
      while (scanner.hasNextLine()) {
        final String line = scanner.nextLine();
        analysis.isSatisfiedWith(line);
      }
    }
  }
}
