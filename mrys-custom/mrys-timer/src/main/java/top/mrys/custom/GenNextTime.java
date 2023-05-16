package top.mrys.custom;

/**
 * @author mrys
 */
@FunctionalInterface
public interface GenNextTime {

  /**
   * 获取下一次执行时间ms
   */
  Long getNextTime();
}
