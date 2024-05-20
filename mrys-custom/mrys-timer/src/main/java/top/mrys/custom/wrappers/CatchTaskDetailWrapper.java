package top.mrys.custom.wrappers;

import top.mrys.custom.BaseTaskDetail;
import top.mrys.custom.TaskDetail;

import java.util.Optional;

/**
 * 该类是TaskDetailWrapper的一个子类，用于提供一种"捕获"机制，即在执行任务详情时，如果发生异常则不会抛出，
 * 而是返回一个空的Optional对象。
 * @param <P> 任务详情参数的类型
 */
public class CatchTaskDetailWrapper<P> extends TaskDetailWrapper<P>{

  /**
   * 构造函数，用于初始化任务详情包装器。
   * @param taskDetail 任务的基础详情对象，不可为null。该对象将被包装并用于实际的任务执行。
   */
  public CatchTaskDetailWrapper(BaseTaskDetail<P> taskDetail) {
    super(taskDetail);
  }

  /**
   * 执行任务详情的call方法，并捕获任何异常，确保不会向外抛出。
   * @return 如果任务详情的call方法成功执行，则返回Optional封装的任务详情对象；如果执行过程中发生异常，则返回空的Optional。
   */
  @Override
  public Optional<TaskDetail> call() throws Exception {
    try {
      // 尝试执行任务详情的call方法
      return getTaskDetail().call();
    } catch (Exception e) {
      // 如果发生异常，则捕获并返回空的Optional对象
      return Optional.empty();
    }
  }
}
