package top.mrys.custom;

/**
 * 任务参数
 *
 * @author mrys
 */
public interface TaskDetail {

  /**
   * 获取任务id
   */
  Long getTaskId();

  String getTaskCode();

  String getTaskName();

  /**
   * 获取下一次执行时间ms
   */
  Long getNextTime();

  /**
   * 获取任务参数
   */
  String getTaskParam();

  /**
   * 获取任务
   */
  Task getTask();

  void setScheduler(TaskScheduler scheduler);

  TaskScheduler getScheduler();

  /**
   * 获取执行次数
   */
  default Integer getExecuteCount() {
    return 0;
  }

  /**
   * 获取执行了多少次
   */
  default Integer getExecuteNum() {
    return 0;
  }

}
