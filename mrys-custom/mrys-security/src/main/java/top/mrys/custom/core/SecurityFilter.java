package top.mrys.custom.core;

import jakarta.servlet.ServletException;

import java.io.IOException;

public interface SecurityFilter {

  void doFilter(ServerExchange exchange, FilterChain chain) throws ServletException, IOException;
}
