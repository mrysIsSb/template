package top.mrys.custom;

import lombok.extern.slf4j.Slf4j;
import top.mrys.custom.wrappers.BaseTaskDetailWrapper;

import java.util.concurrent.ExecutorService;

/**
 * 任务执行器
 *
 * @author mrys
 */
@Slf4j
public class SimpleTaskExecutor implements TaskExecutor {

  private ExecutorService executor;


  public SimpleTaskExecutor(ExecutorService executor) {
    this.executor = executor;
  }


  @Override
  public void execute(TaskDetail taskDetail) {
    executor.submit(() -> {
      try {
        new BaseTaskDetailWrapper(taskDetail).call();
      } catch (Exception e) {
        log.error(e.getMessage(), e);
      }
    });
  }
}
