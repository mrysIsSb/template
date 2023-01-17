package top.mrys.custom.core;

import java.util.Optional;

public class AuthTool {

  /**
   * 获取当前用户
   *
   * @return
   */
  public static <T extends UserInfo> T getUserInfo() {
    return Optional.ofNullable(SecurityContextHolder.getContext())
      .map(SecurityContext::getAuthentication)
      .<T>map(Authentication::getUserInfo)
      .orElse(null);
  }

  public static <T extends UserInfo> Optional<T> getUserInfoOptional() {
    return Optional.ofNullable(getUserInfo());
  }

  /**
   * 判断当前用户是否有角色
   */
  public static boolean hasRole(String role) {
    return getUserInfoOptional()
      .map(UserInfo::getRoles)
      .map(roles -> roles.contains(role))
      .orElse(false);
  }

  /**
   * 判断当前用户是否有权限
   */
  public static boolean hasPermission(String permission) {
    return getUserInfoOptional()
      .map(UserInfo::getPermissions)
      .map(permissions -> permissions.contains(permission))
      .map(bool -> bool || hasRole("admin"))
      .orElse(false);
  }

  public static boolean isLogin() {
    return Optional.ofNullable(SecurityContextHolder.getContext())
      .map(SecurityContext::getAuthentication)
      .map(Authentication::isAuthenticated)
      .orElse(false);
  }

  /**
   * 是否为超级管理员
   *
   * @return
   */
  public static boolean isSuperAdmin() {
    return Optional.<UserInfo>ofNullable(getUserInfo())
      .map(UserInfo::isSuperAdmin)
      .orElse(false);
  }
}
