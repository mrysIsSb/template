package top.mrys.custom.exceptions.handlers;

import java.util.List;
import java.util.Optional;

/**
 * @author mrys
 */
public class ExceptionHandlerRegistry {

  private final List<ExceptionHandler<Throwable>> exceptionHandlers;


  public ExceptionHandlerRegistry() {
    exceptionHandlers = new java.util.ArrayList<>();
  }

  public ExceptionHandlerRegistry(List<ExceptionHandler<Throwable>> exceptionHandlers) {
    this.exceptionHandlers = exceptionHandlers;
  }

  public Optional<ExceptionHandler<Throwable>> getExceptionHandler(Throwable throwable) {
    return exceptionHandlers.stream()
      .filter(handler -> handler.support(throwable))
      .findFirst();
  }

}
