package top.mrys.custom.filters;

import top.mrys.custom.Authentication;

/**
 * access token 认证提供者
 */
public interface AccessTokenAuthenticateProvider {
  Authentication authenticate(AccessTokenAuthentication authentication);
}
