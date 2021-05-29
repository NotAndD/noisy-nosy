package org.just.a.noisynosy.rules;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

public class WatchForTest {

  @Test
  public void should_filter_invalid_rules() {
    final WatchFor watchFor = new WatchFor();
    watchFor.setLogRules(new ArrayList<>());

    final Rule first = Mockito.mock(Rule.class);
    final Rule second = Mockito.mock(Rule.class);
    final Rule third = Mockito.mock(Rule.class);

    Mockito.when(first.isValid()).thenReturn(true);
    Mockito.when(second.isValid()).thenReturn(false);
    Mockito.when(third.isValid()).thenReturn(true);
    watchFor.getLogRules().add(first);
    watchFor.getLogRules().add(second);
    watchFor.getLogRules().add(third);

    watchFor.checkValidity();

    final List<Rule> filteredRules = watchFor.getLogRules();
    Assert.assertTrue(filteredRules != null && filteredRules.size() == 2);
    Assert.assertTrue(filteredRules.contains(first));
    Assert.assertFalse(filteredRules.contains(second));
    Assert.assertTrue(filteredRules.contains(third));
  }

}
