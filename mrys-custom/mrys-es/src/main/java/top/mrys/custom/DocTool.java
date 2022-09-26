package top.mrys.custom;

import java.lang.reflect.Field;

public class DocTool {
  public static Index getIndex(Class<?> clazz) {
    return clazz.getAnnotation(Index.class);
  }

  public static Field getIdField(Class<?> clazz) {
    Field[] fields = clazz.getDeclaredFields();
    for (Field field : fields) {
      if (field.isAnnotationPresent(Id.class)) {
        return field;
      }
    }
    return null;
  }
}
