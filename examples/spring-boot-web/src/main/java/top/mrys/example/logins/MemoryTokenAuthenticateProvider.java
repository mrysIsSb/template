package top.mrys.example.logins;

import org.springframework.stereotype.Component;
import top.mrys.custom.core.Authentication;
import top.mrys.custom.core.UserInfo;
import top.mrys.custom.filters.AccessTokenAuthenticateProvider;
import top.mrys.custom.filters.AccessTokenAuthentication;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author mrys
 */
@Component
public class MemoryTokenAuthenticateProvider implements AccessTokenAuthenticateProvider {

  private final Map<String, UserInfo> users = new ConcurrentHashMap<>();

  private static final String TOKEN_PREFIX = "memory_";

  @Override
  public Authentication authenticate(AccessTokenAuthentication authentication) {
    if (authentication.getAccessToken().startsWith(TOKEN_PREFIX)) {
      UserInfo userInfo = users.get(authentication.getAccessToken());
      if (userInfo != null) {
        authentication.setUserInfo(userInfo);
        authentication.setAuthenticated(true);
        return authentication;
      }
    }
    return null;
  }

  public String addUser(UserInfo userInfo) {
    UUID uuid = UUID.randomUUID();
    String key = String.format("%s%s", TOKEN_PREFIX, uuid);
    users.put(key, userInfo);
    return key;
  }
}
