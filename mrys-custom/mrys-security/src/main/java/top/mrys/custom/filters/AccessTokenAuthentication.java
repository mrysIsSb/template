package top.mrys.custom.filters;

import lombok.Setter;
import top.mrys.custom.core.Authentication;
import top.mrys.custom.core.UserInfo;

public class AccessTokenAuthentication implements Authentication {

  private String accessToken;
  @Setter
  private boolean authenticated;

  private UserInfo userInfo;

  public AccessTokenAuthentication(String accessToken) {
    this.accessToken = accessToken;
    this.authenticated = false;
  }

  @Override
  public boolean isAuthenticated() {
    return authenticated;
  }

  public String getAccessToken() {
    return accessToken;
  }

  public <T extends UserInfo> void setUserInfo(T userInfo) {
    this.userInfo = userInfo;
  }
  @Override
  public <T extends UserInfo> T getUserInfo() {
    return (T) userInfo;
  }
}
