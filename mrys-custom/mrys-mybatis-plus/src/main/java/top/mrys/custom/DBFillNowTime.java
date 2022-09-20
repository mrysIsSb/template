package top.mrys.custom;

import cn.hutool.core.util.ReflectUtil;
import java.lang.reflect.Field;
import java.util.Date;

/**
 * 填充当前时间
 *
 * @author mrys
 */
public class DBFillNowTime implements DBFillData {

  @Override
  public void fill(Object o, Field field, EnumActionType type) {
    ReflectUtil.setFieldValue(o, field, new Date());
  }
}
