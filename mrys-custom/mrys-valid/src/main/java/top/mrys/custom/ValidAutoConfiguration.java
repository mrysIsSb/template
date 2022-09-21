package top.mrys.custom;

import cn.hutool.core.util.StrUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import top.mrys.core.Result;

/**
 * @author mrys
 * @date 2022/9/21
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class ValidAutoConfiguration {


  @Configuration(proxyBeanMethods = false)
//  @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
  @ConditionalOnWebApplication
  @Slf4j
  @RestControllerAdvice
  public static class ValidExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public Result<?> constraintViolationExceptionHandler(ConstraintViolationException e) {
      Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
      String msg = constraintViolations.stream()
        .map(ConstraintViolation::getMessage)
        .collect(Collectors.joining("|"));
      return Result.error(msg);
    }

    @ExceptionHandler(BindException.class)
    public Result<?> bindExceptionHandler(BindException e) {
      List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
      String msg = fieldErrors.stream()
        .map(fieldError -> {
          HashMap<String, Object> map = new HashMap<>();
          map.put("name", fieldError.getField());
          return StrUtil.format(fieldError.getDefaultMessage(), map);
        }).collect(Collectors.joining("|"));
      return Result.error(msg);
    }

    @ExceptionHandler(ValidationException.class)
    public Result<?> validationException(ValidationException e) {
      log.error(e.getMessage(), e);
      return Result.error(e.getMessage());
    }

  }

//  @Bean
//  @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
//  @Order(-10)
//  public WebExceptionHandler webExceptionHandler() {
//    return (exchange, ex) -> {
//      if (ex instanceof ConstraintViolationException) {
//        ConstraintViolationException e = (ConstraintViolationException) ex;
//        return Mono.just(exchange.getResponse())
//          .flatMap(response -> {
//            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
//            return response.writeWith(Mono.just(response.bufferFactory()
//              .wrap(JSONUtil.toJsonStr(Result.error(
//                e.getConstraintViolations().stream().map(ConstraintViolation::getMessage)
//                  .collect(Collectors.joining("|")))).getBytes())));
//          });
//      }
//      return Mono.error(ex);
//    };
//  }

}
