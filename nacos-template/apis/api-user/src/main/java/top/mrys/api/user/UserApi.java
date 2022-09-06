package top.mrys.api.user;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import top.mrys.api.user.dto.UserDTO;
import top.mrys.core.Result;

/**
 * @author mrys
 */
@FeignClient(name = "user", path = UserApi.PATH)
public interface UserApi {

  String PATH = "/user";

  @GetMapping("/get")
  Result<UserDTO> getUser(@RequestParam String username);
}
