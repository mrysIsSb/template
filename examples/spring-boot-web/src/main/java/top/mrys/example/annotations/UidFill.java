package top.mrys.example.annotations;

import org.springframework.core.annotation.AliasFor;
import top.mrys.custom.DBFill;
import top.mrys.custom.EnumActionType;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @author mrys
 */
@Target({java.lang.annotation.ElementType.FIELD})
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@DBFill(UidFillData.class)
public @interface UidFill {

  @AliasFor(annotation = DBFill.class, attribute = "types")
  EnumActionType[] types() default {EnumActionType.INSERT, EnumActionType.UPDATE};

}
