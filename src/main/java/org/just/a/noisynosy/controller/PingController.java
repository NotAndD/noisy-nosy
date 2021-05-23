package org.just.a.noisynosy.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/api")
public class PingController {

  @GetMapping(value = "/ping")
  @ResponseBody
  public ResponseEntity<String> ping() {
    return new ResponseEntity<>("OK", HttpStatus.OK);
  }

}
