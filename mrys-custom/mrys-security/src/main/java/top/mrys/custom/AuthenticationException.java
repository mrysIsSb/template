package top.mrys.custom;

/**
 * 认证异常
 */
public class AuthenticationException extends RuntimeException {
  public AuthenticationException(String message) {
    super(message);
  }
}
