package top.mrys.core;

/**
 * @author mrys
 */
public interface JsonConvertor {

  /**
   * 将对象转换为json字符串
   */
  String toJson(Object obj);

}
