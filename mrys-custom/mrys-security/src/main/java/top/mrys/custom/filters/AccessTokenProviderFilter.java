package top.mrys.custom.filters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import top.mrys.custom.core.FilterChain;
import top.mrys.custom.core.SecurityFilter;
import top.mrys.custom.core.ServerExchange;

import java.util.Objects;

import static top.mrys.custom.filters.OrderConstants.ORDER_ACCESS_TOKEN_PROVIDER;

/**
 * access token filter
 * 获取access token
 *
 * @author mrys
 */
@Slf4j
public class AccessTokenProviderFilter implements SecurityFilter, Ordered {

  @Override
  public void doFilter(ServerExchange exchange, FilterChain chain) {
    //获取access token
    exchange.instanceProvider()
      .getInstances(AccessTokenProvider.class)
      .stream()
      .map(provider -> provider.getAccessToken(exchange))
      .filter(Objects::nonNull)
      .findFirst()//获取第一个
      .ifPresent(accessToken -> {
        log.debug("access token:{}", accessToken);
        //如果存在 设置access token
        exchange.getSecurityContext().setAuthentication(new AccessTokenAuthentication(accessToken));
      });
    chain.doFilter(exchange);
  }

  @Override
  public int getOrder() {
    return ORDER_ACCESS_TOKEN_PROVIDER;
  }
}
