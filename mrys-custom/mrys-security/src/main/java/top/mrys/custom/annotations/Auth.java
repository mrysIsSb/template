package top.mrys.custom.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author mrys
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Auth {
  /**
   * spel表达式
   * #{@beanName.methodName(#args)}
   * @beanName 为spring容器中的bean
   * #alias 为注解别名
   * T(top.mrys.custom.core.AuthTool).hasRole(#alias.value) 为AuthTool类中的hasRole方法
   */
  String value();

  String msg() default "没有权限";
}
