package org.just.a.noisynosy.rules;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class MatchTest {

  @Test
  public void should_be_valid() {
    final Match match = new Match();

    match.setHowMany(2);
    match.setValuesInAnd(new ArrayList<>());
    match.getValuesInAnd().add("aaa");

    Assert.assertTrue(match.isValid());
  }

  @Test
  public void should_be_valid_second() {
    final Match match = new Match();

    match.setHowMany(2);
    match.setHowMuch(200);
    match.setValuesInOr(new ArrayList<>());
    match.getValuesInOr().add("aaa");

    Assert.assertTrue(match.isValid());
  }

  @Test
  public void should_not_be_valid() {
    final Match match = new Match();

    match.setHowMuch(200);
    match.setValuesInOr(new ArrayList<>());
    match.getValuesInOr().add("aaa");

    Assert.assertFalse(match.isValid());
  }

  @Test
  public void should_not_be_valid_forth() {
    final Match match = new Match();

    match.setHowMany(2);
    match.setHowMuch(200);

    Assert.assertFalse(match.isValid());
  }

  @Test
  public void should_not_be_valid_second() {
    final Match match = new Match();

    match.setHowMany(2);
    match.setHowMuch(200);
    match.setValuesInOr(new ArrayList<>());

    Assert.assertFalse(match.isValid());
  }

  @Test
  public void should_not_be_valid_third() {
    final Match match = new Match();

    match.setHowMany(2);
    match.setHowMuch(200);
    match.setValuesInAnd(new ArrayList<>());

    Assert.assertFalse(match.isValid());
  }

}
