package top.mrys.custom.exceptions.handlers;

import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import top.mrys.core.Result;
import top.mrys.core.ResultException;
import top.mrys.custom.core.ServerExchange;

/**
 * @author mrys
 */
public class ResultExceptionHandler implements ExceptionHandler<ResultException>, Ordered {

  @Override
  public boolean support(Throwable throwable) {
    return throwable instanceof ResultException;
  }

  @Override
  public int getOrder() {
    return 0;
  }

  @Override
  public void handle(ServerExchange serverExchange, ResultException e) {
    serverExchange.getResponse().ret(HttpStatus.OK, Result.fail(e.getCode(), e.getMsg()).toJson());
  }
}
