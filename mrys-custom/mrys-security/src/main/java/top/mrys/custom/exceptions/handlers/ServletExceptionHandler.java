package top.mrys.custom.exceptions.handlers;

import cn.hutool.json.JSONUtil;
import jakarta.servlet.ServletException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import top.mrys.core.Result;
import top.mrys.custom.core.ServerExchange;

/**
 * servlet异常处理器
 *
 * @author mrys
 */
@Slf4j
public class ServletExceptionHandler implements ExceptionHandler<ServletException>, Ordered {

  @Override
  public boolean support(Throwable throwable) {
    return throwable instanceof ServletException;
  }

  @Override
  public void handle(ServerExchange serverExchange, ServletException e) {
    log.error(e.getMessage(), e);
    serverExchange.getResponse().ret(HttpStatus.OK, JSONUtil.toJsonStr(Result.fail(e.getRootCause().getMessage())));
  }

  @Override
  public int getOrder() {
    return Ordered.LOWEST_PRECEDENCE - 100;
  }
}
