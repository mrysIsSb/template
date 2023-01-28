package top.mrys.custom;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 放在实体类上
 * 2022-04-13
 * by: mrys
 */
@Target({ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface DBFill {

  Class<? extends DBFillData> value();

  EnumActionType[] types() default {EnumActionType.INSERT, EnumActionType.UPDATE};
}
