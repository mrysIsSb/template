package top.mrys.custom;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @author mrys
 */
@Target({java.lang.annotation.ElementType.TYPE})
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface DBEvent {
  EnumActionType[] value() default {EnumActionType.INSERT, EnumActionType.UPDATE, EnumActionType.DELETE};
}
