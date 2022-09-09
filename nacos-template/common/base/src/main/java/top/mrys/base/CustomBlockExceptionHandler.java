package top.mrys.base;

import cn.hutool.json.JSONUtil;
import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import top.mrys.core.Result;

/**
 * @author mrys
 * @date 2022/9/9
 */
@Configuration
public class CustomBlockExceptionHandler implements BlockExceptionHandler {

  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response, BlockException e)
    throws Exception {
    response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.getWriter()
      .write(JSONUtil.toJsonStr(Result.error("请求过于频繁").map(o -> e.getRule())));
  }

}
