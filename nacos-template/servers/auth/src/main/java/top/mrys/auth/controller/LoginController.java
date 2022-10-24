package top.mrys.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "用户认证")
public class LoginController {

  @Autowired
  private UserApi userApi;

  @GetMapping("/password")
  @Operation(summary = "密码登录")
  @Parameter(name = "username", description = "用户名")
  @Parameter(name = "password", description = "密码")
  public Result<String> password(@RequestParam(required = false) String username, @RequestParam(required = false) String password) {
    return userApi.get(username).mapOK(userDTO -> {
        if (userDTO.getPassword().equals(password)){
          return "登录成功";
        }
      return "登录失败";
    });
  }

}
