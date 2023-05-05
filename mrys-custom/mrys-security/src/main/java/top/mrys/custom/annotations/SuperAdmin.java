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
@Auth(value = "#{T(top.mrys.custom.core.AuthTool).isSuperAdmin()}", msg = "#{'非超级管理员'}")
@AuthAlias(SuperAdmin.class)
public @interface SuperAdmin {
}
