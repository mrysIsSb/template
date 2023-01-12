package top.mrys.custom;

import org.springframework.context.annotation.Import;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @author mrys
 */
@Retention(value = java.lang.annotation.RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.TYPE})
@Import({ApiDocAutoConfiguration.class})
public @interface EnableApiDoc {
}
