package top.mrys.custom.core;

import java.util.LinkedList;
import java.util.List;

public class FilterChain {

  private int index = 0;

  //TODO 2022年10月24日 filters要排序
  private final LinkedList<SecurityFilter> filters=new LinkedList<>();

  public FilterChain(List<SecurityFilter> filters) {
    this.filters.addAll(filters);
  }

  public void doFilter(ServerExchange exchange) {
    if (index < filters.size()) {
      filters.get(index++).doFilter(exchange, this);
    }
  }
}
