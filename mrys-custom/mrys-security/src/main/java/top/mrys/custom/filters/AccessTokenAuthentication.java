package top.mrys.custom.filters;

import top.mrys.custom.Authentication;

public class AccessTokenAuthentication implements Authentication {

  private String accessToken;
  private boolean authenticated;

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
}
