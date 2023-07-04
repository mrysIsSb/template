package top.mrys.custom.excel;

import cn.hutool.core.util.StrUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import top.mrys.custom.poi.ExcelFieldDetail;
import top.mrys.custom.poi.ExcelUtil;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * @author mrys
 */

@Aspect
@Slf4j
public class UploadExcelAspect {

  @Pointcut("@annotation(uploadExcel)")
  public void uploadExcelPointCut(UploadExcel uploadExcel) {
  }

  @SneakyThrows
  @Around("uploadExcelPointCut(uploadExcel)")
  public Object arround(ProceedingJoinPoint joinPoint, UploadExcel uploadExcel) {
    ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    HttpServletRequest request = requestAttributes.getRequest();

    //todo 上传excel ，template 下载excel模板

    Object[] args = joinPoint.getArgs();

    Class<?> clazz = uploadExcel.clazz();
    ExcelUtil util = new ExcelUtil(clazz);
    if (request.getParameter("template") != null) {
      //下载模板
      template(uploadExcel, requestAttributes);
      return null;
    }

    if (request.getParameter("upload") != null) {

      Part part = request.getPart(StrUtil.blankToDefault(uploadExcel.value(), "file"));

      InputStream stream = part.getInputStream();
      List excel = util.importExcel(stream, 0);
      args[0] = excel;

      return joinPoint.proceed(args);
    }
    return joinPoint.proceed(args);
  }

  private void template(UploadExcel uploadExcel, ServletRequestAttributes requestAttributes) {
    ExcelUtil util = new ExcelUtil(uploadExcel.clazz());
    util.dateFormat = "yyyy-MM-dd HH:mm:ss";
    util.fieldFunctions.add((Function<Field, ExcelFieldDetail>) field -> {
      if (!field.isAnnotationPresent(Schema.class)) {
        return null;
      }
      Schema schema = field.getAnnotation(Schema.class);
      if (schema.hidden()) {
        return null;
      }
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
    util.exportExcel(requestAttributes.getResponse(), Collections.emptyList(),"sheet1",uploadExcel.fileName());
  }

}
