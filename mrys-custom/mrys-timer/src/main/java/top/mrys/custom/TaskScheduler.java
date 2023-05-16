package top.mrys.custom;

import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 任务调度
 *
 * @author mrys
 */
public class TaskScheduler {

  //任务仓库
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

  public void start() {

    addTask(new CheckRepoTaskDetail(30 * 1000, this.taskRepo));

    timer = new Timer();
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        lock.writeLock().lock();
        try {
          if (taskQueue.size() == 0) {
            System.out.println("没有任务");
            return;
          }
          TaskDetail detail = taskQueue.remove(0);
          if (detail.getNextTime() <= System.currentTimeMillis()) {
            executeTask(detail);
          } else {
            taskQueue.add(0, detail);
          }
        } finally {
          lock.writeLock().unlock();
        }
      }
    }, 0, 500);
  }

  public void stop() {
    timer.cancel();
    taskQueue.forEach(taskRepo::addTask);
  }

  //添加任务
  public void addTask(TaskDetail taskDetail) {
    Long nextTime = taskDetail.getNextTime();
    //大于1分钟的任务
    if (nextTime > System.currentTimeMillis() + 60 * 1000) {
      taskRepo.addTask(taskDetail);
    } else {
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
  }

  //删除任务
  public void removeTask(TaskDetail taskParam) {
    taskRepo.removeTask(taskParam);
    taskQueue.removeIf(taskDetail -> taskDetail.getTaskId().equals(taskParam.getTaskId()));
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
