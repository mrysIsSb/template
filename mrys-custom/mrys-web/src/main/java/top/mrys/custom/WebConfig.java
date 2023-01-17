package top.mrys.custom;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import top.mrys.core.ResultException;

import java.util.List;

/**
 * @author mrys
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class WebConfig implements WebMvcConfigurer {


  @Override
  public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
    resolvers.add((request, response, handler, ex) -> {
      if (ex instanceof HttpMessageNotReadableException ex1) {
        throw new ResultException(EnumHttpExceptionCode.PARAM_ERROR.getCode(), EnumHttpExceptionCode.PARAM_ERROR.getMsg());
      }
      log.error(ex.getMessage(), ex);
      return null;
    });
  }
}
