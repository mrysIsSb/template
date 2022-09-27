package top.mrys.custom.filters;

import top.mrys.custom.FilterChain;
import top.mrys.custom.SecurityFilter;
import top.mrys.custom.ServerExchange;

public class SecurityContextFilter implements SecurityFilter {
  @Override
  public void doFilter(ServerExchange exchange, FilterChain chain) {
    try {
      chain.doFilter(exchange);
    } finally {
      // 清空securityContext
      exchange.getSecurityContext().clearContext();
    }
  }
}
