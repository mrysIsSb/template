package top.mrys.custom;

public interface SecurityFilter {

  void doFilter(ServerExchange exchange, FilterChain chain);
}
