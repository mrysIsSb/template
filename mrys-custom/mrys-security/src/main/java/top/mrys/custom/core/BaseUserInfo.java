package top.mrys.custom.core;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author mrys
 * @date 2022/12/16 15:00
 */
@Data
public class BaseUserInfo implements UserInfo {

  public BaseUserInfo() {
    this.attrs = new HashMap<>();
  }

  private String userId;
  private String userName;

  private String tenantId;

  private boolean superAdmin = false;

  private List<String> roles;

  private List<String> permissions;

  private Map<String, Object> attrs;

  @Override
  public <T> Optional<T> getAttr(String name) {
    if (attrs == null) {
      return Optional.empty();
    }
    return Optional.ofNullable((T) attrs.get(name));
  }
}
