package top.mrys.custom;

import java.lang.reflect.Field;

/**
 * 2022-04-13 by: mrys
 */
public interface DBFillData {

  void fill(Object o, Field field, EnumActionType type);
}
