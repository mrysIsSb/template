package top.mrys.custom.core;

import top.mrys.custom.exceptions.handlers.ExceptionHandlerRegistry;

import java.util.LinkedList;
import java.util.List;

public class FilterChain {

  private int index = 0;

  //TODO 2022年10月24日 filters要排序 添加前就排序了
  private final LinkedList<SecurityFilter> filters = new LinkedList<>();

  //异常处理器
  private ExceptionHandlerRegistry exceptionHandlerRegistry = new ExceptionHandlerRegistry();

  public FilterChain(List<SecurityFilter> filters) {
    this.filters.addAll(filters);
  }

  public void doFilter(ServerExchange exchange) {
    if (index < filters.size()) {
      try {
        filters.get(index++).doFilter(exchange, this);
      } catch (Throwable e) {
        //所有满足条件的第一个异常处理器处理
        exceptionHandlerRegistry.handle(exchange, e);
      }

    }
  }

  public void setExceptionHandlerRegistry(ExceptionHandlerRegistry exceptionHandlerRegistry) {
    this.exceptionHandlerRegistry = exceptionHandlerRegistry;
  }
}
