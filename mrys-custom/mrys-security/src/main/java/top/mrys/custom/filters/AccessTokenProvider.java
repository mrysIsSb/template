package top.mrys.custom.filters;

import top.mrys.custom.core.ServerExchange;

public interface AccessTokenProvider {

  /**
   * 获取token
   */
  String getAccessToken(ServerExchange exchange);
}
