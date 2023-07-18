package top.mrys.custom;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 任务调度
 *
 * @author mrys
 */
@Slf4j
public class TaskScheduler {

  //任务仓库
  @Getter
  private final TaskRepo taskRepo;

  //任务执行器
  private final TaskExecutor taskExecutor;
  //lock
  private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

  public TaskScheduler(TaskRepo taskRepo, TaskExecutor taskExecutor) {
    this.taskRepo = taskRepo;
    this.taskExecutor = taskExecutor;
  }

  private Timer timer;

  @Getter
  private AtomicBoolean started = new AtomicBoolean(false);

  public void start() {
    if (started.compareAndSet(false, true)) {
      taskRepo.init();
      timer = new Timer();
      timer.schedule(new TimerTask() {
        @Override
        public void run() {
          lock.writeLock().lock();
          try {
            while (true) {
              List<TaskDetail> details = taskRepo.takeWaitingTask(System.currentTimeMillis());

              if (details == null || details.size() == 0) {
                return;
              }

              for (TaskDetail detail : details) {
                executeTask(detail);
              }
            }
          } catch (Exception e) {
            log.error(e.getMessage(), e);
          } finally {
            lock.writeLock().unlock();
          }
        }
      }, 0, 500);
    }
  }

  /**
   * 停止
   */
  public void stop() {
    if (started.compareAndSet(true, false)) {
      timer.cancel();
    }
  }

  /**
   * 执行任务
   *
   * @param detail
   */
  public void executeTask(TaskDetail detail) {
    detail.setScheduler(this);
    taskExecutor.execute(detail);
  }

}
