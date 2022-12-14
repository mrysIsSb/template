package top.mrys.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author mrys
 * @version 1.0
 * @date 2022/12/13 10:09
 */
@RestController
@RequestMapping("/test")
public class TestController {

  @GetMapping("/test1")
  public String test1(String name) {
    return "test1" + name;
  }
}
