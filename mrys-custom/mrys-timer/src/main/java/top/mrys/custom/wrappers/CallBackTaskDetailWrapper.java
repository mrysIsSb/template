package top.mrys.custom.wrappers;

import top.mrys.custom.TaskDetail;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * 一个抽象类，用于封装BaseTaskDetail对象，提供TaskDetail接口的实现。
 */
public class CallBackTaskDetailWrapper extends TaskDetailWrapper {

  /**
   * 是否继续下次执行
   */
  private boolean isContinue = false;

  private Consumer<TaskDetail> onSuccess;

  private BiConsumer<TaskDetail, Exception> onFail;

  /**
   * 构造函数，用于初始化任务详情包装器。
   *
   * @param taskDetail 任务的基础详情对象，不可为null。
   */
  public CallBackTaskDetailWrapper(TaskDetail taskDetail, boolean isContinue) {
    super(taskDetail);
    this.isContinue = isContinue;
  }

  public CallBackTaskDetailWrapper onSuccess(Consumer<TaskDetail> onSuccess) {
    this.onSuccess = onSuccess;
    return this;
  }

  public CallBackTaskDetailWrapper onFail(BiConsumer<TaskDetail, Exception> onFail) {
    this.onFail = onFail;
    return this;
  }

  @Override
  public Optional<TaskDetail> call() throws Exception {
    Optional<TaskDetail> detail = Optional.empty();
    try {
      detail = getTaskDetail().call();
      if (onSuccess != null) {
        detail.ifPresent(onSuccess);
      }
    } catch (Exception e) {
      if (onFail != null) {
        onFail.accept(getTaskDetail(), e);
      }
      throw e;
    }
    if (detail.isPresent() && isContinue) {
      CallBackTaskDetailWrapper wrapper = new CallBackTaskDetailWrapper(detail.get(), isContinue);
      wrapper.onSuccess(onSuccess);
      wrapper.onFail(onFail);
      detail = Optional.of(wrapper);
    }
    return detail;
  }
}
