package top.mrys.custom;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import top.mrys.custom.wrappers.CallBackTaskDetailWrapper;
import top.mrys.custom.wrappers.CatchTaskDetailWrapper;
import top.mrys.custom.wrappers.RetryTaskDetailWrapper;

import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author mrys
 */
@Slf4j

public class TestScheduler {

  @Test
  public void test1() throws InterruptedException {
    log.info("开始测试任务调度器。");
    TaskScheduler scheduler = new TaskScheduler(new SimpleTaskExecutor(Executors.newSingleThreadExecutor()));
    scheduler.start();
//    scheduler.register(new BaseTaskDetailWrapper(new CatchTaskDetailWrapper(new RetryTaskDetailWrapper(new Task1()))));
    TaskDetail taskDetail = new CatchTaskDetailWrapper(new RetryTaskDetailWrapper(new Task2()));
    taskDetail = new CallBackTaskDetailWrapper(taskDetail, true)
      .onSuccess(t -> log.info("成功"))
      .onFail((t, e) -> log.error("失败", e));
    scheduler.register(taskDetail);
    for (int i = 0; i < 3; i++) {
      scheduler.executeTask("task2", t -> log.info("task2执行"), (t, e) -> log.error("task2执行失败", e));
      TimeUnit.SECONDS.sleep(1);
    }
    while (true) {
    }
  }
}

@Slf4j
class Task1 extends BaseTaskDetail<Void> {
  @Override
  public void registered(TaskScheduler scheduler) {
    super.registered(scheduler);
    setName("task1");
    setID("task1");
  }

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
  public void registered(TaskScheduler scheduler) {
    super.registered(scheduler);
    setID("task2");
  }

  @Override
  public Optional<TaskDetail> call() throws Exception {
    log.info("task2{}", this);
    this.setNextTime(System.currentTimeMillis() + 4000);
    i++;
    if (i % 3 != 0) {
      log.info("task2异常");
      throw new RuntimeException("task2");
    }
    log.info("task2正常");
    return Optional.of(this);
  }
}