package top.mrys.custom.exceptions;

/**
 * 没有登录异常
 * @author mrys
 */
public class NoLoginException extends RuntimeException {

  public NoLoginException() {
    super("请登录");
  }

  public NoLoginException(String msg, Throwable cause) {
    super(msg, cause);
  }

  private static NoLoginException instance = new NoLoginException();

  public static NoLoginException getInstance(){
    return instance;
  }
}
