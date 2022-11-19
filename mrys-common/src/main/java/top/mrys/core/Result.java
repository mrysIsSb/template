package top.mrys.core;

import java.util.Optional;
import java.util.function.Function;

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
//    return """
//      Result{code=%d, msg='%s', data=%s}
//      """.formatted(code, msg, data);
    return "Result{code=%d, msg='%s', data=%s}".formatted(code, msg, data);
  }

  // ----------------------override end----------------------
  // ----------------------method begin----------------------

  /**
   * 数据转换
   *
   * @param mapper 转换函数
   * @param <O>    转换后的类型
   * @return 转换后的结果
   * @author mrys
   */
  public <O> Result<O> map(Function<T, O> mapper) {
    return new Result<>(code, msg, mapper.apply(data));
  }

  /**
   * 数据转换
   *
   * @param mapper 转换函数
   * @param <O>    转换后的类型
   * @return 转换后的结果
   */
  public <O> Result<O> mapOK(Function<T, O> mapper) {
    return map(0, mapper);
  }

  /**
   * 数据转换
   *
   * @param code   状态码
   * @param mapper 转换函数
   * @param <O>    转换后的类型
   * @return 转换后的结果
   * @author mrys
   */
  public <O> Result<O> map(Integer code, Function<T, O> mapper) {
    if (this.code == code) {
      return new Result<>(code, msg, mapper.apply(data));
    }
    return new Result<>(this.code, this.msg, null);
  }

  public Optional<T> optional() {
    return Optional.ofNullable(data);
  }

  public Result<T> then(Function<T, Result<T>> function) {
    if (isOk()) {
      return function.apply(data);
    }
    return this;
  }


  public Result<T> throwIfError() throws ResultException {
    return throwIfError(msg);
  }

  public Result<T> throwIfError(String msg) throws ResultException {
    if (!isOk()) {
      throw new ResultException(code, msg);
    }
    return this;
  }

  public Boolean isOk() {
    return code == 0;
  }

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

  public static <T> Result<T> error(int code, String msg) {
    return newInstance(code, msg, null);
  }
  public static <T> Result<T> assert0(boolean b, String msg, T data) {
    if (b) {
      return ok(data);
    }
    return error(msg);
  }
  // ----------------------static method end----------------------
}
