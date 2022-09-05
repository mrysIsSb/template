package top.mrys.core;

/**
 * @author mrys
 */
public class ResultException extends RuntimeException {

  private int code;

  private String msg;

  public ResultException(int code, String msg) {
    super(msg);
    this.code = code;
    this.msg = msg;
  }

  public ResultException(int code, String msg, Throwable cause) {
    super(msg, cause);
    this.code = code;
    this.msg = msg;
  }

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }

  public String getMsg() {
    return msg;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }
}

