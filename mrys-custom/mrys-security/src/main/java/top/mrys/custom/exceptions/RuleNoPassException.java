package top.mrys.custom.exceptions;

/**
 * 规则不通过异常
 * @author mrys
 */
public class RuleNoPassException extends RuntimeException{
  public RuleNoPassException(String message) {
    super(message);
  }
}
