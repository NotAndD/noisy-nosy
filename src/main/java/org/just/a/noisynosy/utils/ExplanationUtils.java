package org.just.a.noisynosy.utils;

public final class ExplanationUtils {

  public static String limitTo(String message, int maxChars, boolean fromEnd) {
    if (message == null || message.length() <= maxChars) {
      return message;
    }

    return message.substring(fromEnd ? message.length() - maxChars : 0,
        fromEnd ? message.length() : maxChars);
  }

  private ExplanationUtils() {
    super();
  }

}
