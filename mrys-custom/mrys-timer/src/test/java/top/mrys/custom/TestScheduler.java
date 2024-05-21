package top.mrys.custom;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import top.mrys.custom.wrappers.BaseTaskDetailWrapper;
import top.mrys.custom.wrappers.CatchTaskDetailWrapper;
import top.mrys.custom.wrappers.RetryTaskDetailWrapper;

import java.util.Optional;
import java.util.concurrent.Executors;

/**
 * @author mrys
 */
@Slf4j

public class TestScheduler {

  @Test
  public void test1() {
    log.info("开始测试任务调度器。");
    TaskScheduler scheduler = new TaskScheduler(new SimpleTaskExecutor(Executors.newSingleThreadExecutor()));
    scheduler.start();
//    scheduler.register(new BaseTaskDetailWrapper(new CatchTaskDetailWrapper(new RetryTaskDetailWrapper(new Task1()))));
    scheduler.register(new BaseTaskDetailWrapper(new CatchTaskDetailWrapper(new RetryTaskDetailWrapper(new Task2()))));
    while (true) {
    }
  }
}

@Slf4j
class Task1 extends BaseTaskDetail<Void> {

  @Override
  public Optional<TaskDetail> call() throws Exception {
    log.info("task1{}", this);
    this.setNextTime(System.currentTimeMillis() + 1000);
    return Optional.of(this);
  }
}

@Slf4j
class Task2 extends BaseTaskDetail<Void> {
  int i = 0;

  @Override
  public Optional<TaskDetail> call() throws Exception {
    log.info("task2{}", this);
    this.setNextTime(System.currentTimeMillis() + 2000);
    i++;
    if (i % 3 != 0) {
      log.info("task2异常");
      throw new RuntimeException("task2");
    }
    log.info("task2正常");
    return Optional.of(this);
  }
}