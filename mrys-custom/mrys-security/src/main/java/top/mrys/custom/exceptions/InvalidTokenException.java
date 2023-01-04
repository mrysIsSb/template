package top.mrys.custom.exceptions;

/**
 * 无效的token
 * @author mrys
 */
public class InvalidTokenException extends RuntimeException{
    public InvalidTokenException() {
        super("无效的token");
    }
}
