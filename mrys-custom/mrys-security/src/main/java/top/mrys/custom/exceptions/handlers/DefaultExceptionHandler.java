package top.mrys.custom.exceptions.handlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import top.mrys.core.Result;
import top.mrys.custom.core.ServerExchange;

/**
 * @author mrys
 */
@Slf4j
public class DefaultExceptionHandler implements ExceptionHandler<Throwable>, Ordered {

  @Override
  public void handle(ServerExchange serverExchange, Throwable e) {
    log.warn(e.getMessage());
    serverExchange.getResponse().ret(HttpStatus.OK, Result.fail(e.getMessage()).toJson());
  }

  @Override
  public int getOrder() {
    return Ordered.LOWEST_PRECEDENCE;
  }
}
