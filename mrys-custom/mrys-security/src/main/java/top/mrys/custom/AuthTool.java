package top.mrys.custom;

public class AuthTool {

  /**
   * 获取当前用户
   *
   * @return
   */
  public static <T extends UserInfo> T  getUserInfo() {
    return SecurityContextHolder.getContext().getAuthentication().getUserInfo();
  }

  /**
   * 判断当前用户是否有角色
   */
  public static boolean hasRole(String role){
    UserInfo userInfo = getUserInfo();
    if (userInfo == null) {
      return false;
    }
    return userInfo.getRoles().contains(role);
  }

  /**
   * 判断当前用户是否有权限
   */
  public static boolean hasPermission(String permission){
    UserInfo userInfo = getUserInfo();
    if (userInfo == null) {
      return false;
    }
    return userInfo.getPermissions().contains(permission);
  }

  public static boolean isLogin() {
    return SecurityContextHolder.getContext().getAuthentication().isAuthenticated();
  }

}
