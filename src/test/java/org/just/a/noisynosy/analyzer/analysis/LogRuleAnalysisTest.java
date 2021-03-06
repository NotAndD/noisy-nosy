package org.just.a.noisynosy.analyzer.analysis;

import org.junit.Assert;
import org.junit.Test;
import org.just.a.noisynosy.rules.Rule;
import org.just.a.noisynosy.utils.RuleMockUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.stream.Collectors;

public class LogRuleAnalysisTest {

  @Test
  public void rule_should_match_word_aaa() {
    final String shortLog = "Hey there!\n"
        + "Did I tell you of that time where\n"
        + "I went to aaa and, I dunno how\n"
        + "but I found myself in bbb instead?\n"
        + "Crazy, right?\n"
        + "Luckily I reached aaa just in time for\n"
        + "the aaa-event which was there, wuff.";

    final Rule rule = RuleMockUtils.givenLogRuleInAnd(
        RuleMockUtils.givenLogMatchInOr(3, "aaa", "ccc"));
    final RuleAnalysis analysis = new RuleAnalysis(rule);
    makeAnalysisConsumeLog(analysis, shortLog);

    Assert.assertTrue(analysis.isSatisfied());
    Assert.assertEquals("I went to aaa and, I dunno how\n"
        + "Luckily I reached aaa just in time for\n"
        + "the aaa-event which was there, wuff.",
        analysis.getSatisfiedExplanation());
  }

  @Test
  public void rule_should_match_word_ccc() {
    final String shortLog = "Hey there!\n"
        + "Did I tell you of that time where\n"
        + "I went to aaa and, I dunno how\n"
        + "but I found myself in bbb instead?\n"
        + "Crazy, right? I decided to change plans.\n"
        + "Luckily I reached ccc just in time for\n"
        + "the ccc-event which was there, wuff.";

    final Rule rule = RuleMockUtils.givenLogRuleInAnd(
        RuleMockUtils.givenLogMatchInOr(2, "aaa", "ccc"));
    final RuleAnalysis analysis = new RuleAnalysis(rule);
    makeAnalysisConsumeLog(analysis, shortLog);

    Assert.assertTrue(analysis.isSatisfied());
    Assert.assertEquals("I went to aaa and, I dunno how\n"
        + "Luckily I reached ccc just in time for\n"
        + "the ccc-event which was there, wuff.",
        analysis.getSatisfiedExplanation());
  }

  @Test
  public void rule_should_match_word_overlay() {
    final String shortLog = "Huhu\n"
        + "the overlay sometimes\n"
        + "is as incredible as an error\n"
        + "or an Exception which is unexpected\n"
        + "usually, except when the overlay\n"
        + "is activated.";

    final Rule rule = RuleMockUtils.givenLogRuleInOr(
        RuleMockUtils.givenLogMatchInAnd(3, "sometimes"),
        RuleMockUtils.givenLogMatchInAnd(2, "overlay"));
    final RuleAnalysis analysis = new RuleAnalysis(rule);
    makeAnalysisConsumeLog(analysis, shortLog);

    Assert.assertTrue(analysis.isSatisfied());
    Assert.assertEquals("the overlay sometimes\n"
        + "usually, except when the overlay",
        analysis.getSatisfiedExplanation());
  }

  @Test
  public void rule_should_match_word_sometimes() {
    final String shortLog = "Welcome!\n"
        + "sometimes things work\n"
        + "and sometimes they doesn't\n"
        + "\n"
        + "(most of the times)\n"
        + "but sometimes, just sometimes..\n"
        + "things are perfect.";

    final Rule rule = RuleMockUtils.givenLogRuleInAnd(
        RuleMockUtils.givenLogMatchInAnd(3, "sometimes"));
    final RuleAnalysis analysis = new RuleAnalysis(rule);

    makeAnalysisConsumeLog(analysis, shortLog);

    Assert.assertTrue(analysis.isSatisfied());
    Assert.assertEquals("sometimes things work\n"
        + "and sometimes they doesn't\n"
        + "but sometimes, just sometimes..",
        analysis.getSatisfiedExplanation());
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

    final Rule rule = RuleMockUtils.givenLogRuleInAnd(
        RuleMockUtils.givenLogMatchInAnd(1, "Exception", "caused by"));
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

    final Rule rule = RuleMockUtils.givenLogRuleInAnd(
        RuleMockUtils.givenLogMatchInAnd(1, "foo"),
        RuleMockUtils.givenLogMatchInAnd(1, "bar"));
    final RuleAnalysis analysis = new RuleAnalysis(rule);

    makeAnalysisConsumeLog(analysis, shortLog);

    Assert.assertTrue(analysis.isSatisfied());
  }

  @Test
  public void rule_should_not_match_word_ccc() {
    final String shortLog = "Hey there!\n"
        + "Did I tell you of that time where\n"
        + "I went to aaa and, I dunno how\n"
        + "but I found myself in bbb instead?\n"
        + "Crazy, right? I decided to change plans.\n"
        + "Luckily I reached ccc just in time for\n"
        + "the ccc-event which was there, wuff.";

    final Rule rule = RuleMockUtils.givenLogRuleInAnd(
        RuleMockUtils.givenLogMatchInOr(4, "aaa", "ccc"));
    final RuleAnalysis analysis = new RuleAnalysis(rule);
    makeAnalysisConsumeLog(analysis, shortLog);

    Assert.assertFalse(analysis.isSatisfied());
  }

  @Test
  public void rule_should_not_match_word_overlay() {
    final String shortLog = "Huhu\n"
        + "the overlay sometimes\n"
        + "is as incredible as an error\n"
        + "or an Exception which is unexpected\n"
        + "usually, except when the overlay\n"
        + "is activated.";

    final Rule rule = RuleMockUtils.givenLogRuleInAnd(
        RuleMockUtils.givenLogMatchInAnd(3, "sometimes"),
        RuleMockUtils.givenLogMatchInAnd(2, "overlay"));
    final RuleAnalysis analysis = new RuleAnalysis(rule);
    makeAnalysisConsumeLog(analysis, shortLog);

    Assert.assertFalse(analysis.isSatisfied());
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

    final Rule rule = RuleMockUtils.givenLogRuleInAnd(
        RuleMockUtils.givenLogMatchInAnd(3, "sometimes"));
    final RuleAnalysis analysis = new RuleAnalysis(rule);

    makeAnalysisConsumeLog(analysis, shortLog);

    Assert.assertFalse(analysis.isSatisfied());
  }

  @Test
  public void should_reset_if_it_was_satisfied() {
    final String shortLog = "Hey there!\n"
        + "Did I tell you of that time where\n"
        + "I went to aaa and, I dunno how\n"
        + "but I found myself in bbb instead?\n"
        + "Crazy, right?\n"
        + "Luckily I reached aaa just in time for\n"
        + "the aaa-event which was there, wuff.";

    final Rule rule = RuleMockUtils.givenLogRuleInAnd(
        RuleMockUtils.givenLogMatchInOr(3, "aaa", "ccc"));
    final RuleAnalysis analysis = new RuleAnalysis(rule);
    makeAnalysisConsumeLog(analysis, shortLog);

    analysis.reset();

    Assert.assertFalse(analysis.isSatisfied());
  }

  @Test
  public void should_work_with_long_text_1() {
    final InputStream stream =
        LogRuleAnalysisTest.class.getResourceAsStream("/log/long-text-1.txt");
    final String longLog = new BufferedReader(new InputStreamReader(stream))
        .lines().collect(Collectors.joining("\n"));

    final Rule rule = RuleMockUtils.givenLogRuleInAnd(
        RuleMockUtils.givenLogMatchInAnd(3, "Something"));
    final RuleAnalysis analysis = new RuleAnalysis(rule);

    makeAnalysisConsumeLog(analysis, longLog);

    Assert.assertTrue(analysis.isSatisfied());
  }

  @Test
  public void should_work_with_long_text_2() {
    final InputStream stream =
        LogRuleAnalysisTest.class.getResourceAsStream("/log/long-text-2.txt");
    final String longLog = new BufferedReader(new InputStreamReader(stream))
        .lines().collect(Collectors.joining("\n"));

    final Rule rule = RuleMockUtils.givenLogRuleInAnd(
        RuleMockUtils.givenLogMatchInAnd(1, "caused by", "Exception"),
        RuleMockUtils.givenLogMatchInAnd(5, "Something"));
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
