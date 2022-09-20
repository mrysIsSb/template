package top.mrys.custom;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import org.springframework.core.annotation.AnnotatedElementUtils;

/**
 * @author mrys
 */
public class AnnotationTool {

  /**
   * 获取注解
   */
  public static <A extends Annotation> A findMergedAnnotation(AnnotatedElement element, Class<A> clazz) {
    return AnnotatedElementUtils.findMergedAnnotation(element, clazz);
  }

  public static boolean hasAnnotation(AnnotatedElement element, Class<? extends Annotation> clazz) {
    return AnnotatedElementUtils.hasAnnotation(element, clazz);
  }

}
