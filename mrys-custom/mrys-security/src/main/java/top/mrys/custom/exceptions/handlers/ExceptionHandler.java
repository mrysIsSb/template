package top.mrys.custom.exceptions.handlers;

import top.mrys.custom.core.ServerExchange;

/**
 * 异常处理器
 *
 * @author mrys
 */
public interface ExceptionHandler<E extends Throwable> {


  default boolean support(Throwable throwable) {
    return true;
  }

  /**
   * 处理异常
   *
   * @param e
   * @return
   */
  void handle(ServerExchange serverExchange, E e);

}
