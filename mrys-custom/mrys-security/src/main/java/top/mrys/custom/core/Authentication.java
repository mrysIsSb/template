package top.mrys.custom.core;

public interface Authentication {

  /**
   * 是否已经认证
   */
  boolean isAuthenticated();

  <T extends UserInfo> T getUserInfo();

}
