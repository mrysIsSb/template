package top.mrys.custom;

/**
 * 任务执行器
 *
 * @author mrys
 */
public interface TaskExecutor {

  /**
   * 执行任务
   *
   * @param taskDetail 任务详情
   */
  void execute(TaskDetail taskDetail);
}
