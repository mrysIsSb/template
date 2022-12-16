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

}
