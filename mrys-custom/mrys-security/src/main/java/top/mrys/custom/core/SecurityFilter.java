package top.mrys.custom.core;

public interface SecurityFilter {

  void doFilter(ServerExchange exchange, FilterChain chain);
}
