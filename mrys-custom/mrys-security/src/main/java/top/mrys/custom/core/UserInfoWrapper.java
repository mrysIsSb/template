package top.mrys.custom.core;

import java.util.List;
import java.util.Optional;

/**
 * 用户信息包装类
 *
 * @author mrys
 */
public class UserInfoWrapper implements UserInfo {
  private final UserInfo userInfo;

  public UserInfoWrapper(UserInfo userInfo) {
    this.userInfo = userInfo;
  }

  public static UserInfoWrapper of(UserInfo userInfo) {
    return new UserInfoWrapper(userInfo);
  }

  /**
   * 获取原始的用户信息
   */
  public UserInfo getRawUserInfo() {
    return userInfo;
  }


  @Override
  public String getUserId() {
    return userInfo.getUserId();
  }

  @Override
  public String getUserName() {
    return userInfo.getUserName();
  }

  @Override
  public String getTenantId() {
    return userInfo.getTenantId();
  }

  @Override
  public boolean isSuperAdmin() {
    return userInfo.isSuperAdmin();
  }

  @Override
  public List<String> getRoles() {
    return userInfo.getRoles();
  }

  @Override
  public List<String> getPermissions() {
    return userInfo.getPermissions();
  }

  @Override
  public <T> Optional<T> getAttr(String name) {
    return userInfo.getAttr(name);
  }
}
