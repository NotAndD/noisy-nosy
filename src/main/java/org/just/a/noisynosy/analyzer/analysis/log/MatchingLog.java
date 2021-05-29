package org.just.a.noisynosy.analyzer.analysis.log;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MatchingLog {

  private String line;
  private Date when;

  public boolean isAfter(long time) {
    return when.getTime() > time;
  }

}
