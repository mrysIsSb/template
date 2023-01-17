package top.mrys.custom.exceptions.handlers;

import cn.hutool.json.JSONUtil;
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
    serverExchange.getResponse().ret(HttpStatus.OK, JSONUtil.toJsonStr(Result.fail(e.getMessage())));
  }

  @Override
  public int getOrder() {
    return Ordered.LOWEST_PRECEDENCE;
  }
}
