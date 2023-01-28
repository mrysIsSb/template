package top.mrys.custom.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import top.mrys.custom.core.BaseUserInfo;
import top.mrys.custom.core.UserInfo;

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

    private String password;

    private boolean superAdmin;

    private String tenantId;

    private List<String> roles;

    private List<String> permissions;

    public UserInfo toUserInfo(){
      BaseUserInfo info = new BaseUserInfo();
      info.setUserId(this.getUserId());
      info.setUserName(this.getUserName());
      info.setSuperAdmin(this.isSuperAdmin());
      info.setRoles(this.getRoles());
      info.setPermissions(this.getPermissions());
      info.setTenantId(this.getTenantId());
      return info;
    }
  }
}
