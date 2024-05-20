package top.mrys.custom.wrappers;

import lombok.Getter;
import lombok.Setter;
import top.mrys.custom.BaseTaskDetail;
import top.mrys.custom.TaskDetail;

import java.util.Optional;

/**
 * 为任务详情提供重试机制的包装类。该类继承自TaskDetailWrapper，添加了重试功能。
 * 在执行任务时，如果任务执行失败，将根据设定的重试次数进行重试。
 *
 * @param <P> 任务详情的参数类型
 */
public class RetryTaskDetailWrapper<P> extends TaskDetailWrapper<P> {

  private static final int DEFAULT_RETRY_TIMES = 3; // 默认重试次数

  private final int retryTimes; // 重试次数

  @Getter
  @Setter
  private int times = 0; // 当前重试次数

  /**
   * 构造函数，用于初始化RetryTaskDetailWrapper。
   *
   * @param taskDetail 任务详情对象，不允许为null。该任务详情对象将被包装并提供重试机制。
   */
  public RetryTaskDetailWrapper(BaseTaskDetail<P> taskDetail) {
    this(taskDetail, DEFAULT_RETRY_TIMES);
  }

  public RetryTaskDetailWrapper(BaseTaskDetail<P> taskDetail, int retryTimes) {
    super(taskDetail);
    this.retryTimes = retryTimes;
  }

  /**
   * 执行任务并提供重试机制。如果任务执行失败且当前重试次数小于设定的重试次数，则重新执行任务。
   *
   * @return Optional<TaskDetail> 如果任务执行成功，返回包含任务详情的Optional对象；如果任务执行失败且已达到最大重试次数，则返回空的Optional。
   * @throws Exception 如果任务执行过程中抛出异常，且当前重试次数未达到设定的最大重试次数，则将当前重试次数加一并重新执行任务，否则将异常抛出。
   */
  @Override
  public Optional<TaskDetail> call() throws Exception {
    try {
      return getTaskDetail().call(); // 尝试执行任务
    } catch (Exception e) {
      BaseTaskDetail<P> taskDetail = getTaskDetail();
      // 判断当前重试次数是否小于设定的重试次数，如果是，则增加重试次数并重新加入执行队列
      if (getTimes() < retryTimes) {
        setTimes(getTimes() + 1);
        taskDetail.getScheduler().addTask(this);
      }
      throw e; // 如果已经达到最大重试次数，抛出异常
    }
  }
}
