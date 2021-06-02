package org.just.a.noisynosy.analyzer.analysis;

public interface MatchAnalysis {

  String getSatisfiedExplanation();

  boolean isSatisfied();

  boolean isSatisfiedWith(Object obj);

  void reset();

}
