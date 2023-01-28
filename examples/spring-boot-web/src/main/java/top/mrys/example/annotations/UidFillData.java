package top.mrys.example.annotations;

import cn.hutool.core.util.ReflectUtil;
import org.springframework.stereotype.Component;
import top.mrys.custom.DBFillData;
import top.mrys.custom.EnumActionType;
import top.mrys.custom.core.AuthTool;

import java.lang.reflect.Field;

/**
 * @author mrys
 */
@Component
public class UidFillData implements DBFillData {
  @Override
  public void fill(Object o, Field field, EnumActionType enumActionType) {
    AuthTool.getUserInfoOptional()
      .ifPresent(userInfo -> ReflectUtil.setFieldValue(o, field, userInfo.getUserId()));
  }
}
