package top.mrys.custom.exceptions.handlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import top.mrys.custom.core.ServerExchange;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author mrys
 */
@Slf4j
public class ExceptionHandlerRegistry implements ExceptionHandler {

  private final List<ExceptionHandler> exceptionHandlers;

  private ThreadLocal<Set<ExceptionHandler>> handled = new ThreadLocal<>();


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

      getExceptionHandler(throwable).ifPresent(handler -> {
        Set<ExceptionHandler> handlers = handled.get();
        if (handlers == null) {
          handlers = new java.util.HashSet<>();
        }
        if (handlers.contains(handler)) {
          log.error(throwable.getMessage(), throwable);
          serverExchange.getResponse().ret(HttpStatus.INTERNAL_SERVER_ERROR, "循环异常处理器");
        } else {
          handlers.add(handler);
          handled.set(handlers);
          handler.handle(serverExchange, throwable);
        }
      });
    } catch (RuntimeException e) {
      //如果异常处理器处理异常失败，继续循环
      handle(serverExchange, e);
    } catch (Throwable e) {
      log.error(e.getMessage(), e);
    } finally {
      handled.remove();
    }
  }
}
