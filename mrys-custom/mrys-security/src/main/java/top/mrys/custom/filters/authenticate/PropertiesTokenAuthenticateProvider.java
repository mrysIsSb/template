package top.mrys.custom.filters.authenticate;

import top.mrys.custom.config.SecurityProperties;
import top.mrys.custom.core.Authentication;
import top.mrys.custom.filters.AccessTokenAuthenticateProvider;
import top.mrys.custom.filters.AccessTokenAuthentication;

import java.util.List;

/**
 * properties token 认证提供者
 * @author mrys
 * @date 2022/12/16 14:11
 */
public class PropertiesTokenAuthenticateProvider implements AccessTokenAuthenticateProvider {

  private final List<SecurityProperties.User> users;

  public PropertiesTokenAuthenticateProvider(List<SecurityProperties.User> users) {
    this.users = users;
  }

  @Override
  public Authentication authenticate(AccessTokenAuthentication authentication) {
    String accessToken = authentication.getAccessToken();
    users.stream()
      .filter(user -> user.getToken().equals(accessToken))
      .findFirst()
      .ifPresent(user -> {
        authentication.setAuthenticated(true);
        authentication.setUserInfo(user.toUserInfo());
      });
    return authentication;
  }
}
