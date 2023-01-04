package top.mrys.custom.exceptions;

/**
 * 鉴权异常
 *
 * @author mrys
 */
public class AuthenticationException extends RuntimeException {

    public AuthenticationException(String msg) {
        super(msg);
    }

    public AuthenticationException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
