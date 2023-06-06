package top.mrys.custom;

import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import top.mrys.core.ResultException;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author mrys
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class WebConfig implements WebMvcConfigurer {

  @Resource
  private Optional<List<CustomExceptionHandler>> exceptionHandlers;


  @Override
  public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
    resolvers.add((request, response, handler, ex) -> {
      if (ex instanceof HttpMessageNotReadableException ex1) {
        throw new ResultException(EnumHttpExceptionCode.PARAM_ERROR.getCode(),
          StrUtil.format("{}:({})", EnumHttpExceptionCode.PARAM_ERROR.getMsg(), ex1.getMessage()));
      }
      AtomicBoolean handled = new AtomicBoolean(false);
      exceptionHandlers.ifPresent(handlers ->
        handlers.stream().map(handler1 ->
            handler1.handler(request, response, handler, ex))
          .filter(b -> b).findFirst().ifPresent(handled::set));
      if (handled.get()) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setStatus(HttpStatusCode.valueOf(response.getStatus()));
        return modelAndView;
      }

      if (ex instanceof ResultException ex1) {
        log.warn(ex1.getMessage());
        throw ex1;
      }
      log.error(ex.getMessage(), ex);
      return null;
    });
  }
}
