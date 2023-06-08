package top.mrys.custom.excel;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import top.mrys.core.PageParam;
import top.mrys.core.PageResult;
import top.mrys.custom.poi.ExcelFieldDetail;
import top.mrys.custom.poi.ExcelUtil;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

/**
 * 2022-07-06
 * by: mrys
 */
@Aspect
@Slf4j
public class DownLoadExcelAspect {

  @Pointcut("@annotation(downLoadExcel)")
  public void downLoadExcelPointCut(DownLoadExcel downLoadExcel) {
  }

  @SneakyThrows
  @Around("downLoadExcelPointCut(downLoadExcel)")
  public Object arround(ProceedingJoinPoint joinPoint, DownLoadExcel downLoadExcel) {
    ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    HttpServletRequest request = requestAttributes.getRequest();
    Object[] args = joinPoint.getArgs();
    boolean isExport = false;
    if (request.getParameter("export") != null) {
      isExport = true;
      args = Arrays.stream(args).map(o -> {
        if (o instanceof PageParam) {
          PageParam pageParam = (PageParam) o;
          pageParam.setSize(0L);
          pageParam.setCurrent(0L);
          return pageParam;
        } else {
          return o;
        }
      }).toArray();
    }
    Object proceed = joinPoint.proceed(args);
    if (!isExport) {
      return proceed;
    }
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Class<?> returnType = signature.getMethod().getReturnType();
    List list = null;
    if (returnType.isAssignableFrom(PageResult.class)) {
      PageResult<?> result = (PageResult<?>) joinPoint.proceed();
      list = result.getData();
    } else {
      list = (List) joinPoint.proceed();
    }
    if (CollUtil.isNotEmpty(list)) {
      list = JSONUtil.parseArray(list).toList(downLoadExcel.clazz());
      export(downLoadExcel, requestAttributes, list);
    }
    return proceed;
  }

  private void export(DownLoadExcel downLoadExcel, ServletRequestAttributes requestAttributes,
                      List list) {
    ExcelUtil util = new ExcelUtil(downLoadExcel.clazz());
    util.dateFormat = "yyyy-MM-dd HH:mm:ss";
    util.fieldFunctions.add((Function<Field, ExcelFieldDetail>) field -> {
      if (!field.isAnnotationPresent(Schema.class)) {
        return null;
      }
      Schema schema = field.getAnnotation(Schema.class);
      ExcelFieldDetail detail = new ExcelFieldDetail();
      field.setAccessible(true);
      detail.setField(field);
      detail.setFieldName(schema.description());
      detail.setPrompt(schema.description());
      ExcelValueMapper excelValueMapper = field.getAnnotation(ExcelValueMapper.class);
      if (excelValueMapper != null) {
        detail.setHandler(excelValueMapper.value());
      }
      return detail;
    });
    util.exportExcel(requestAttributes.getResponse(), list, downLoadExcel.value(),
      downLoadExcel.value());
  }
}
