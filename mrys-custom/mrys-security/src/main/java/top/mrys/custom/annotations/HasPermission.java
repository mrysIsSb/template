package top.mrys.custom.annotations;

import java.lang.annotation.Target;

/**
 * @author mrys
 */
@Target({java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.TYPE})
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Auth(value = "#{T(top.mrys.custom.AuthTool).hasPermission(#alias.value)}", msg = "#{'没有['+ T(cn.hutool.core.util.StrUtil).blankToDefault(#alias.desc,#alias.value) +']权限'}")
@AuthAlias(HasPermission.class)
public @interface HasPermission {
    /**
     * 权限
     */
    String value();

    /**
     * 权限描述
     */
    String desc() default "";
}
