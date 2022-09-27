package top.mrys.custom;

import java.util.LinkedList;
import java.util.List;

public class FilterChain {

  private int index = 0;

  private LinkedList<SecurityFilter> filters;

  public FilterChain(List<SecurityFilter> filters) {
    this.filters.addAll(filters);
  }

  public void doFilter(ServerExchange exchange) {
    if (index < filters.size()) {
      filters.get(index++).doFilter(exchange, this);
    }
  }
}
