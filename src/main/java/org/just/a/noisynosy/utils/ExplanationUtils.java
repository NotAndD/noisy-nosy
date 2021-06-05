package org.just.a.noisynosy.utils;

public final class ExplanationUtils {

  public static String limitTo(String message, int maxChars, boolean fromEnd) {
    String result = message;
    if (result.length() > maxChars) {
      result = result.substring(fromEnd ? result.length() - maxChars : 0,
          fromEnd ? result.length() : maxChars);
    } ;

    return result;
  }

  private ExplanationUtils() {
    super();
  }

}
