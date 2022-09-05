package top.mrys.core;

/**
 * @author mrys
 */
public class Result<T> {

  private int code;
  private String msg;
  private T data;

  // ----------------------base begin----------------------
  public Result() {
  }

  public Result(int code, String msg, T data) {
    this.code = code;
    this.msg = msg;
    this.data = data;
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

  public T getData() {
    return data;
  }

  public void setData(T data) {
    this.data = data;
  }
  // ----------------------base end----------------------

  // ----------------------override begin----------------------
  @Override
  public String toString() {
    return """
      Result{code=%d, msg='%s', data=%s}
      """.formatted(code, msg, data);
  }
  // ----------------------override end----------------------
  // ----------------------method begin----------------------

  // ----------------------method end------------------------

  // ----------------------static method begin----------------------

  public static <T> Result<T> newInstance() {
    return new Result<>();
  }

  public static <T> Result<T> newInstance(int code, String msg, T data) {
    return new Result<>(code, msg, data);
  }

  public static <T> Result<T> ok() {
    return ok(null);
  }

  public static <T> Result<T> ok(T data) {
    return newInstance(0, "ok", data);
  }

  public static <T> Result<T> error() {
    return error(null);
  }

  public static <T> Result<T> error(String msg) {
    return newInstance(-1, msg, null);
  }
  // ----------------------static method end----------------------
}
