package top.mrys.custom.excel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author mrys
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface UploadExcel {

  /**
   * 上传的文件字段
   */
  String value() default "file";

  Class<?> clazz();

  String fileName() default "数据";
}
