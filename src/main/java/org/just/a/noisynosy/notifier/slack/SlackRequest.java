package org.just.a.noisynosy.notifier.slack;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SlackRequest {

  private String text;
}
