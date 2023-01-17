package top.mrys.core;

import java.util.Optional;
import java.util.ServiceLoader;
import java.util.function.Function;

/**
 * @author mrys
 */
public class Result<T> {

  private int code;
  private String msg;
  private T data;

  public static int SUCCESS = 0;
  public static int FAIL = -1;

  private static final ServiceLoader<JsonConvertor> convertors = ServiceLoader.load(JsonConvertor.class);

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

  public String toJson() {
    return convertors.findFirst().orElseThrow().toJson(this);
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
  public <O> Result<O> mapSuccess(Function<T, O> mapper) {
    return map(SUCCESS, mapper);
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
    if (isSuccess()) {
      return function.apply(data);
    }
    return this;
  }


  public Result<T> throwIfError() throws ResultException {
    return throwIfError(msg);
  }

  public Result<T> throwIfError(String msg) throws ResultException {
    if (!isSuccess()) {
      throw new ResultException(code, msg);
    }
    return this;
  }

  public Boolean isSuccess() {
    return code == SUCCESS;
  }

  // ----------------------method end------------------------

  // ----------------------static method begin----------------------

  public static <T> Result<T> newInstance() {
    return new Result<>();
  }

  public static <T> Result<T> newInstance(int code, String msg, T data) {
    return new Result<>(code, msg, data);
  }

  public static <T> Result<T> success() {
    return success(null);
  }

  public static <T> Result<T> success(T data) {
    return newInstance(0, "ok", data);
  }

  public static <T> Result<T> fail() {
    return fail(null);
  }

  public static <T> Result<T> fail(String msg) {
    return newInstance(FAIL, msg, null);
  }

  public static <T> Result<T> fail(int code, String msg) {
    return newInstance(code, msg, null);
  }
  public static <T> Result<T> assert0(boolean b, String msg, T data) {
    if (b) {
      return success(data);
    }
    return fail(msg);
  }
  // ----------------------static method end----------------------
}
