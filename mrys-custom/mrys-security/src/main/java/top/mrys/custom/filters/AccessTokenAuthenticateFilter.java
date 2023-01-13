package top.mrys.custom.filters;

import org.springframework.core.Ordered;
import top.mrys.custom.core.FilterChain;
import top.mrys.custom.core.SecurityFilter;
import top.mrys.custom.core.ServerExchange;
import top.mrys.custom.exceptions.InvalidTokenException;

/**
 * access token filter
 *
 * @author mrys
 */
public class AccessTokenAuthenticateFilter implements SecurityFilter, Ordered {
  @Override
  public void doFilter(ServerExchange exchange, FilterChain chain) {
    if (exchange.getSecurityContext().getAuthentication() == null ||
      exchange.getSecurityContext().getAuthentication().isAuthenticated() ||
      !(exchange.getSecurityContext().getAuthentication() instanceof AccessTokenAuthentication authentication)) {
      // 如果没有认证信息 或者 已经认证过了 或者 不是access token 认证
      chain.doFilter(exchange);
    } else {
      exchange.getSecurityContext()
        .setAuthentication(exchange.instanceProvider()
          .getInstances(AccessTokenAuthenticateProvider.class)
          .stream()
          .map(provider -> provider.authenticate(authentication))
          .filter(auth -> auth != null && auth.isAuthenticated())//
          .findFirst()
          .orElseThrow(InvalidTokenException::new));//如果没有认证成功 抛出异常
      chain.doFilter(exchange);
    }
  }

  @Override
  public int getOrder() {
    return OrderConstants.ORDER_ACCESS_TOKEN_AUTHENTICATE;
  }
}
