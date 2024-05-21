package top.mrys.custom.wrappers;

import lombok.extern.slf4j.Slf4j;
import top.mrys.custom.BaseTaskDetail;
import top.mrys.custom.TaskDetail;

import java.util.Optional;

/**
 * 为BaseTaskDetail类型提供包装功能的类，继承自TaskDetailWrapper。
 *
 * @param <P> 任务详情的泛型参数类型
 */
@Slf4j
public class BaseTaskDetailWrapper extends TaskDetailWrapper {

  /**
   * 构造函数，初始化BaseTaskDetailWrapper。
   *
   * @param taskDetail 任务详情对象，不允许为null。
   */
  public BaseTaskDetailWrapper(TaskDetail taskDetail) {
    super(taskDetail);
  }

  /**
   * 执行任务并管理执行次数与调度。
   * 1. 增加任务的执行次数。
   * 2. 调用任务的call方法。
   * 3. 如果任务执行成功，则将任务添加到调度器中。
   *
   * @return 返回任务执行的结果，可能为空。
   * @throws Exception 如果任务的call方法抛出异常，则此处也会抛出异常。
   */
  @Override
  public Optional<TaskDetail> call() throws Exception {
    log.debug("--> {}", this.getClass());
    TaskDetail taskDetail = getTaskDetail();
    // 增加任务执行次数
    if (taskDetail instanceof BaseTaskDetail baseTaskDetail) {
      baseTaskDetail.setExecTimes(taskDetail.getExecTimes() + 1);
    }
    // 执行任务并获取结果
    Optional<TaskDetail> called = taskDetail.call().map(BaseTaskDetailWrapper::new);
    // 如果任务执行成功，将其添加到调度器中
    called.ifPresent(taskDetail.getScheduler()::addTask);
    return called;
  }
}

