package top.mrys.custom;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @author mrys
 * @date 2022/12/16 14:17
 */
@Data
@ConfigurationProperties(prefix = "security")
public class SecurityProperties {

  private Local local;

  @Data
  public static class Local {
    private boolean enable;//是否启用本地用户

    private List<User> users;
  }

  @Data
  public static class User {
    private String token;

    private String userId;

    private String userName;

    private List<String> roles;

    private List<String> permissions;
  }
}
