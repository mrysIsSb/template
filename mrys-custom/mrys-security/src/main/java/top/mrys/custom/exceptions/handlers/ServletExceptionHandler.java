package top.mrys.custom.exceptions.handlers;

import jakarta.servlet.ServletException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
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
  @SneakyThrows
  public void handle(ServerExchange serverExchange, ServletException e) {
    log.error("url:{},msg:{}", serverExchange.getRequest().getPath(), e.getMessage());
    throw e.getRootCause();
  }

  @Override
  public int getOrder() {
    return Ordered.LOWEST_PRECEDENCE - 100;
  }
}
