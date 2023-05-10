package top.mrys.custom.excel;

import top.mrys.custom.poi.ExcelHandlerAdapter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 2022-07-06
 * by: mrys
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelValueMapper {

  Class<? extends ExcelHandlerAdapter> value();
}
