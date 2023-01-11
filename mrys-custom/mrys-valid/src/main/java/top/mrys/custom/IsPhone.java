package top.mrys.custom;

import jakarta.validation.Constraint;
import jakarta.validation.OverridesAttribute;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Pattern;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 手机号
 *
 * @author mrys
 */
@Constraint(validatedBy = {})
@Pattern(regexp = "^1[3-9]\\d{9}$")
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface IsPhone {

  @OverridesAttribute(constraint = Pattern.class, name = "message")
  String message() default "{top.mrys.core.IsPhone.message}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

}
