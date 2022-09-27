package top.mrys.custom.filters;

import top.mrys.custom.Authentication;

public interface AccessTokenAuthenticateProvider {
  Authentication authenticate(AccessTokenAuthentication authentication);
}
