package top.mrys.custom.filters;

import top.mrys.custom.core.Authentication;

/**
 * access token 认证提供者
 */
public interface AccessTokenAuthenticateProvider {
  Authentication authenticate(AccessTokenAuthentication authentication);
}
