package top.mrys.custom.wrappers;

import top.mrys.custom.TaskDetail;
import top.mrys.custom.TaskScheduler;

/**
 * 一个抽象类，用于封装BaseTaskDetail对象，提供TaskDetail接口的实现。
 *
 * @param <P> 任务详情参数的类型。
 */
public abstract class TaskDetailWrapper implements TaskDetail {
  // 任务基础详情对象，封装了任务的详细信息和行为。
  private final TaskDetail taskDetail;

  /**
   * 构造函数，用于初始化任务详情包装器。
   *
   * @param taskDetail 任务的基础详情对象，不可为null。
   */
  public TaskDetailWrapper(TaskDetail taskDetail) {
    this.taskDetail = taskDetail;
  }

  /**
   * 获取基础任务详情对象。
   *
   * @return 返回封装的基础任务详情对象。
   */
  public TaskDetail getTaskDetail() {
    return taskDetail;
  }

  /**
   * 获取下一个执行时间。
   *
   * @return 返回任务的下一个执行时间，以Long型表示的毫秒数。
   */
  @Override
  public Long getNextTime() {
    return taskDetail.getNextTime();
  }

  /**
   * 获取任务名称。
   *
   * @return 返回任务的名称，不可为null。
   */
  @Override
  public String getName() {
    return taskDetail.getName();
  }

  /**
   * 获取任务已执行次数。
   *
   * @return 返回任务已经执行的次数，以Long型表示。
   */
  @Override
  public Long getExecTimes() {
    return taskDetail.getExecTimes();
  }

  /**
   * 向任务调度器注册当前任务。
   *
   * @param scheduler 任务调度器对象，用于调度任务执行。
   */
  @Override
  public void registered(TaskScheduler scheduler) {
    taskDetail.registered(scheduler);
  }

  /**
   * 获取任务调度器。
   * 该方法不接受任何参数。
   *
   * @return TaskScheduler 实例，用于任务的调度。
   */
  @Override
  public TaskScheduler getScheduler() {
    return taskDetail.getScheduler();
  }
}
