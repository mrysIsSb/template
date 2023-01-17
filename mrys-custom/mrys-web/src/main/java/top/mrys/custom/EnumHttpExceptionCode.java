package top.mrys.custom;

import lombok.Getter;

/**
 * @author mrys
 */
@Getter
public enum EnumHttpExceptionCode {
  //请求参数错误
  PARAM_ERROR(-1001, "请求参数错误"),
  ;
  private final int code;
  private final String msg;


  EnumHttpExceptionCode(int code, String msg) {
    this.code = code;
    this.msg = msg;
  }
}
