package top.mrys.custom.filters;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import top.mrys.custom.FilterChain;
import top.mrys.custom.SecurityFilter;
import top.mrys.custom.ServerExchange;

@Slf4j
public class SecurityContextFilter implements SecurityFilter, Ordered {
  @Override
  public void doFilter(ServerExchange exchange, FilterChain chain) {
    try {
      chain.doFilter(exchange);
    } finally {
      // 清空securityContext
      log.debug("清空 security context");
      exchange.getSecurityContext().clearContext();
    }
  }

  @Override
  public int getOrder() {
    return 0;
  }
}
