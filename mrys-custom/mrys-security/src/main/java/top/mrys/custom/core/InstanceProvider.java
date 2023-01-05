package top.mrys.custom.core;

import java.util.List;

/**
 * 实例提供者
 *
 * @author mrys
 */
public interface InstanceProvider {

  /**
   * 获取实例
   */
  <T> T getInstance(Class<T> clazz);

  /**
   * 获取实例
   */
  <T> List<T> getInstances(Class<T> clazz);
}
