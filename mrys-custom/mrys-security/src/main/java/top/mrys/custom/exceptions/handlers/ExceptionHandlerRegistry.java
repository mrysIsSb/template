package top.mrys.custom.exceptions.handlers;

import lombok.extern.slf4j.Slf4j;
import top.mrys.custom.core.ServerExchange;

import java.util.List;
import java.util.Optional;

/**
 * @author mrys
 */
@Slf4j
public class ExceptionHandlerRegistry implements ExceptionHandler {

  private final List<ExceptionHandler> exceptionHandlers;


  public ExceptionHandlerRegistry() {
    exceptionHandlers = new java.util.ArrayList<>();
  }

  public ExceptionHandlerRegistry(List<ExceptionHandler> exceptionHandlers) {
    this.exceptionHandlers = exceptionHandlers;
  }

  public Optional<ExceptionHandler> getExceptionHandler(Throwable throwable) {
    return exceptionHandlers.stream()
      .filter(handler -> handler.support(throwable))
      .findFirst();
  }


  @Override
  public void handle(ServerExchange serverExchange, Throwable throwable) {
    //循环所有的异常处理器
    try {
      getExceptionHandler(throwable).ifPresent(handler -> handler.handle(serverExchange, throwable));
    } catch (RuntimeException e) {
      //如果异常处理器处理异常失败，继续循环
      handle(serverExchange, e);
    } catch (Throwable e) {
      log.error(e.getMessage(), e);
    }
  }
}
