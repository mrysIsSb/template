package top.mrys.custom;

import java.util.Optional;
import java.util.concurrent.Callable;

/**
 * 任务详情接口，定义了任务的基本信息和行为。
 *
 * @author mrys
 */
public interface TaskDetail extends Callable<Optional<TaskDetail>> {

  /**
   * 获取任务的名字。
   *
   * @return 任务的名称。
   */
  String getName();

  /**
   * 注册TaskScheduler到当前任务。
   *
   * @param scheduler 要注册的TaskScheduler实例。
   */
  void register(TaskScheduler scheduler);

  /**
   * 获取下一次执行任务的时间（以毫秒为单位）。
   *
   * @return 下一次执行任务的时间戳。
   */
  Long getNextTime();

  /**
   * 获取任务已经执行的次数。
   *
   * @return 任务执行的次数。
   */
  Long getExecTimes();

}
