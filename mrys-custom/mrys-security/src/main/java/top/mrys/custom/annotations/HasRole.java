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
@Auth(value = "#{T(top.mrys.custom.AuthTool).hasRole(#alias.value)}", msg = "#{'非['+#alias.value+']角色'}")
@AuthAlias(HasRole.class)
public @interface HasRole {
  String value();
}
