package top.mrys.custom.excel;

import cn.hutool.core.util.StrUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import top.mrys.custom.poi.ExcelUtil;

import java.io.InputStream;
import java.util.List;

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
      util.exportExcel(requestAttributes.getResponse());
      return null;
    }

    if (request.getParameter("upload") == null) {

      Part part = request.getPart(StrUtil.blankToDefault(uploadExcel.value(), "file"));

      InputStream stream = part.getInputStream();
      List excel = util.importExcel(stream, 2);
      args[0] = excel;

      return joinPoint.proceed(args);
    }
    return joinPoint.proceed(args);
  }

}
