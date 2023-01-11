package top.mrys.custom.emtitys;

import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.Getter;

/**
 * @author mrys
 */
@Getter
public enum EnumDelFlag implements IEnum<Integer> {
  NOT_DEL(1, "未删除"),
  DEL(2, "删除");
  ;
  private final int code;
  private final String name;

  EnumDelFlag(int code, String name) {
    this.code = code;
    this.name = name;
  }


  @Override
  public Integer getValue() {
    return getCode();
  }


}
