package top.mrys.api.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import top.mrys.api.user.dto.UserDTO;
import top.mrys.core.Result;

/**
 * @author mrys
 */
@Tag(name = "用户")
@FeignClient(name = "user", path = UserApi.PATH)
public interface UserApi {

  String PATH = "/user";

  @GetMapping("/get")
  @Operation(summary = "查询用户")
  Result<UserDTO> get(@RequestParam String username);
}
