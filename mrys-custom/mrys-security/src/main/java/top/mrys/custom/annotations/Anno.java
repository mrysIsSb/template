package top.mrys.custom.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @author mrys
 * 不用授权接口
 * @date 2023/1/3 17:31
 */
@Target({java.lang.annotation.ElementType.METHOD, java.lang.annotation.ElementType.TYPE})
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
public @interface Anno {
}
