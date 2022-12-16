package top.mrys.custom.filters;

import lombok.extern.slf4j.Slf4j;
import top.mrys.custom.FilterChain;
import top.mrys.custom.SecurityFilter;
import top.mrys.custom.ServerExchange;

import java.util.Objects;

/**
 * access token filter
 * 获取access token
 *
 * @author mrys
 */
@Slf4j
public class AccessTokenProviderFilter implements SecurityFilter {

  @Override
  public void doFilter(ServerExchange exchange, FilterChain chain) {
    //获取access token
    exchange.instanceProvider()
      .getInstances(AccessTokenProvider.class)
      .stream()
      .map(provider -> provider.getAccessToken(exchange))
      .filter(Objects::nonNull)
      .findFirst()
      .ifPresent(accessToken -> {
        log.debug("access token:{}", accessToken);
        //如果存在 设置access token
        exchange.getSecurityContext().setAuthentication(new AccessTokenAuthentication(accessToken));
      });
    chain.doFilter(exchange);
  }
}
