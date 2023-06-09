package top.mrys.custom;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
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

  //任务队列
  private final List<TaskDetail> taskQueue;

  //lock
  private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

  public TaskScheduler(TaskRepo taskRepo, TaskExecutor taskExecutor) {
    this.taskRepo = taskRepo;
    this.taskExecutor = taskExecutor;
    this.taskQueue = new LinkedList<>();
  }

  private Timer timer;

  private Timer timer2;

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
              if (taskQueue.size() == 0) {
                return;
              }
              TaskDetail detail = taskQueue.remove(0);
              if (detail.getNextTime() <= System.currentTimeMillis()) {
                executeTask(detail);
              } else {
                taskQueue.add(0, detail);
                return;
              }
            }
          } catch (Exception e) {
            log.error(e.getMessage(), e);
          } finally {
            lock.writeLock().unlock();
          }
        }
      }, 0, 500);

      timer2 = new Timer();
      timer2.schedule(new TimerTask() {
        @Override
        public void run() {
          try {
            List<TaskDetail> taskDetails = taskRepo.takeWaitingTask();
            lock.writeLock().lock();
            taskDetails.forEach(t -> {
              Long nextTime = t.getNextTime();
              int index = 0;
              for (int i = taskQueue.size() - 1; i >= 0; i--) {
                TaskDetail task = taskQueue.get(i);
                if (task.getNextTime() < nextTime) {
                  index = i + 1;
                  break;
                }
              }
              taskQueue.add(index, t);
            });
          } catch (Exception e) {
            log.error(e.getMessage(), e);
          } finally {
            lock.writeLock().unlock();
          }
        }
      }, 0, 30 * 1000);
    }
  }

  public void stop() {
    if (started.compareAndSet(true, false)) {
      timer.cancel();
      timer2.cancel();
      taskQueue.clear();
    }
  }

  //添加任务
  public void addTask(TaskDetail taskDetail) {
    Long nextTime = taskDetail.getNextTime();
    Integer taskStatus = taskDetail.getTaskStatus();
    if (taskStatus == null) {
      taskDetail.setTaskStatus(0);
    }
    if (taskStatus == 3) {
      taskRepo.addTask(taskDetail);
      return;
    }
    //大于1分钟的任务
    if (nextTime > System.currentTimeMillis() + 60 * 1000) {
      taskDetail.setTaskStatus(0);
    } else {
      taskDetail.setTaskStatus(1);
      lock.writeLock().lock();
      try {
        int index = 0;
        for (int i = taskQueue.size() - 1; i >= 0; i--) {
          TaskDetail task = taskQueue.get(i);
          if (task.getNextTime() < nextTime) {
            index = i + 1;
            break;
          }
        }
        taskQueue.add(index, taskDetail);
      } finally {
        lock.writeLock().unlock();
      }
    }
    taskRepo.addTask(taskDetail);
  }

  //删除任务
  public void removeTask(TaskDetail detail) {
    taskRepo.removeTask(detail);
    taskQueue.removeIf(taskDetail -> taskDetail.getTaskId().equals(detail.getTaskId()) || taskDetail.getTaskCode().equals(detail.getTaskCode()));
  }

  //修改任务
  public void modifyTask(TaskDetail detail) {

  }

  //执行任务
  public void executeTask(TaskDetail detail) {
    detail.setScheduler(this);
    taskExecutor.execute(detail);
  }

  //暂停任务
  public void pauseTask(TaskDetail detail) {

  }
}
