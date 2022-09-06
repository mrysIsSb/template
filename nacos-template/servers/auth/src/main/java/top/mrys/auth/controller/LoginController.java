package top.mrys.auth.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.mrys.api.user.UserApi;
import top.mrys.core.Result;

/**
 * @author mrys
 */
@RestController
@RequestMapping("/login")
public class LoginController {

  @Autowired
  private UserApi userApi;

  @GetMapping("/password")
  public Result<String> password(@RequestParam String username, @RequestParam String password) {
    return userApi.getUser(username).matOK(userDTO -> {
        if (userDTO.getPassword().equals(password)) {
          return "登录成功";
        }
      return "登录失败";
    });
  }

}
