package top.mrys.custom;

import java.util.concurrent.ExecutorService;
import java.util.function.Function;

/**
 * 任务执行器
 *
 * @author mrys
 */
public class SimpleTaskExecutor implements TaskExecutor {

  private ExecutorService executorService;

  private Function<Task, Task> taskProxy = Function.identity();

  public SimpleTaskExecutor(ExecutorService executorService) {
    this.executorService = executorService;
  }

  public SimpleTaskExecutor(ExecutorService executorService, Function<Task, Task> taskProxy) {
    this.executorService = executorService;
    this.taskProxy = taskProxy;
  }

  @Override
  public void execute(TaskDetail taskDetail) {
    executorService.execute(() -> {
      Task task = taskProxy.apply(taskDetail.getTask());
      TaskRet ret = task.execute(taskDetail);
      if (ret == null) {
        return;
      }
      ret.getNewTaskDetail().ifPresent(newDetail -> taskDetail.getScheduler().addTask(newDetail));
    });
  }
}
