package top.mrys.user.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.mrys.api.user.UserApi;
import top.mrys.api.user.dto.UserDTO;
import top.mrys.core.Result;

/**
 * @author mrys
 */
@RestController
@RequestMapping(UserApi.PATH)
public class UserController implements UserApi {

  @Override
  public Result<UserDTO> getUser(String username) {
    if ("mrys".equals(username)) {
      UserDTO userDTO = new UserDTO();
      userDTO.setUsername("mrys");
      userDTO.setPassword("123456");
      return Result.ok(userDTO);
    }
    return Result.error("用户不存在");
  }
}
