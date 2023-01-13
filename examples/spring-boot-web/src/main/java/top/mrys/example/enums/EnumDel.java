package top.mrys.example.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import lombok.Getter;

/**
 * @author mrys
 */
@Getter
public enum EnumDel implements IEnum<Integer> {
  DEL(1, "删除"),
  NOT_DEL(0, "未删除");
  ;
  private final int code;
  private final String desc;

  EnumDel(int code, String desc) {
    this.code = code;
    this.desc = desc;
  }

  @Override
  public Integer getValue() {
    return getCode();
  }
}
