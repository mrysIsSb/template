package top.mrys.example.enums;

import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;

/**
 * @author mrys
 */
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum EnumDel implements IEnum<Integer> {
  DEL(1, "删除"),
  NOT_DEL(0, "正常");
  ;
  private final int code;
  private final String desc;

  EnumDel(int code, String desc) {
    this.code = code;
    this.desc = desc;
  }

  @Override
  @JsonIgnore
  public Integer getValue() {
    return getCode();
  }
}
