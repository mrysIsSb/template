package top.mrys.custom.core;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author mrys
 * @date 2022/12/16 15:00
 */
@Data
public class BaseUserInfo implements UserInfo {

  private String userId;
  private String userName;

  private String tenantId;

  private boolean superAdmin = false;

  private List<String> roles;

  private List<String> permissions;

  private Map<String , Object> attrs;

  @Override
  public <T> T getAttr(String name) {
    return (T) attrs.get(name);
  }
}
