package top.mrys.controller;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.mrys.custom.AuthTool;
import top.mrys.custom.UserInfo;

import java.util.concurrent.TimeUnit;

/**
 * @author mrys
 * @version 1.0
 * @date 2022/12/13 10:09
 */
@RestController
@RequestMapping("/test")
@Slf4j
public class TestController {

  @GetMapping("/test1")
  @SneakyThrows
  public String test1(String name) {
    TimeUnit.SECONDS.sleep(10);
    UserInfo userInfo = AuthTool.getUserInfo();
    log.info("current user {}", userInfo);
    return "test1" + name;
  }
}
