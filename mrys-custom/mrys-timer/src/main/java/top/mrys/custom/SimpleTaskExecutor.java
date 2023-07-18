package top.mrys.custom;

import java.util.concurrent.Executor;
import java.util.function.Function;

/**
 * 任务执行器
 *
 * @author mrys
 */
public class SimpleTaskExecutor implements TaskExecutor {

  private Executor executor;

  private Function<Task, Task> taskProxy = Function.identity();

  public SimpleTaskExecutor(Executor executor) {
    this.executor = executor;
  }

  public SimpleTaskExecutor(Executor executor, Function<Task, Task> taskProxy) {
    this.executor = executor;
    this.taskProxy = taskProxy;
  }

  @Override
  public void execute(TaskDetail taskDetail) {
    executor.execute(() -> {
      Task task = taskProxy.apply(taskDetail.getTask());
      task.execute(taskDetail);
    });
  }
}
