package top.mrys.custom;

/**
 * @author mrys
 */
public interface Task {

  /**
   * 任务执行
   */
  TaskRet execute(TaskDetail param);
}
