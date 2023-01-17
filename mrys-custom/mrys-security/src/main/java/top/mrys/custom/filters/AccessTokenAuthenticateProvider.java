package top.mrys.custom.filters;

import top.mrys.custom.core.Authentication;

/**
 * access token 认证提供者
 */
public interface AccessTokenAuthenticateProvider {
  /**
   * 认证 access token
   */
  Authentication authenticate(AccessTokenAuthentication authentication);
}
