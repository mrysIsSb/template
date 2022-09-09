package top.mrys.base;

import cn.hutool.core.util.StrUtil;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import top.mrys.core.Result;
import top.mrys.core.ResultException;

/**
 * 2021-08-02 by: mrys
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {


  /**
   * 处理ResultException
   * @param e
   */
  @ExceptionHandler(ResultException.class)
  public Result<?> resultExceptionHandler(ResultException e) {
    log.error(e.getMessage(), e);
    return Result.newInstance(e.getCode(), e.getMsg(), null);
  }

  /**
   * 参数类型转换错误
   *
   * @param e 错误
   * @return 错误信息
   */
  @ExceptionHandler(HttpMessageConversionException.class)
  public Result<?> parameterTypeException(HttpMessageConversionException e) {
    log.error(e.getMessage(), e);
    return Result.error();
  }

  // <1> 处理 form data方式调用接口校验失败抛出的异常
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

  // <3> 处理单个参数校验失败抛出的异常
  @ExceptionHandler(ConstraintViolationException.class)
  public Result<?> constraintViolationExceptionHandler(ConstraintViolationException e) {
    Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
    String msg = constraintViolations.stream()
      .map(constraintViolation -> {
        HashMap<String, Object> map = new HashMap<>();
        if (constraintViolation.getPropertyPath() instanceof PathImpl) {
          map.put("name",
            ((PathImpl) constraintViolation.getPropertyPath()).getLeafNode().getName());
        }
        return StrUtil.format(constraintViolation.getMessage(), map);
      })
      .collect(Collectors.joining("|"));
    return Result.error(msg);
  }


  @ExceptionHandler(ValidationException.class)
  public Result<?> validationException(ValidationException e) {
    log.error(e.getMessage(), e);
    return Result.error(e.getMessage());
  }

  @ExceptionHandler(UndeclaredThrowableException.class)
  public Result<?> UndeclaredThrowableException(UndeclaredThrowableException e) {
    log.error(e.getMessage(), e);
    if (e.getCause() instanceof BlockException){
      BlockException blockException = (BlockException) e.getCause();
      return Result.error("系统繁忙，请稍后再试").map(o->blockException.getRule());
    }
    return Result.error(e.getMessage());
  }

  /**
   * 处理RunTimeException异常
   */
  @ExceptionHandler(RuntimeException.class)
  public Result<?> runtimeExceptionHandler(RuntimeException e) {
    log.error(e.getMessage(), e);
    return Result.error(e.getMessage());
  }

  @ExceptionHandler(Throwable.class)
  public Result<?> throwableHandler(Throwable e) {
    log.error(e.getMessage(), e);
    return Result.error(e.getMessage());
  }

}
