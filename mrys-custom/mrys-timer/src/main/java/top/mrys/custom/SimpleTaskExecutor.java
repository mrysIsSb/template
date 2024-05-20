package top.mrys.custom;

import java.util.concurrent.ExecutorService;

/**
 * 任务执行器
 *
 * @author mrys
 */
public class SimpleTaskExecutor implements TaskExecutor {

  private ExecutorService executor;


  public SimpleTaskExecutor(ExecutorService executor) {
    this.executor = executor;
  }


  @Override
  public void execute(TaskDetail taskDetail) {
    executor.submit(taskDetail);
  }
}
