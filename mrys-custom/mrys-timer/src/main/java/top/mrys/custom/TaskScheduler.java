package top.mrys.custom;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 任务调度类，负责管理和执行计划任务。
 *
 * @author mrys
 */
@Slf4j
public class TaskScheduler {


  // 任务执行器，负责具体执行任务逻辑
  private final TaskExecutor taskExecutor;
  // 锁，用于控制对任务队列的并发访问
  private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

  // 任务队列，按照任务的执行时间顺序排列
  private final LinkedList<TaskDetail> taskQueue = new LinkedList<>();

  /**
   * 构造函数，初始化任务调度器。
   *
   * @param taskExecutor 任务执行器
   */
  public TaskScheduler(TaskExecutor taskExecutor) {
    this.taskExecutor = taskExecutor;
  }

  // 定时器执行服务，用于按照预定间隔执行任务
  private ScheduledExecutorService executorService;

  /**
   * 启动标志，标识任务调度器是否已启动。
   */
  @Getter
  private AtomicBoolean started = new AtomicBoolean(false);

  /**
   * 注册任务到调度器。
   *
   * @param taskDetail 待注册的任务详情
   */
  public void register(TaskDetail taskDetail) {
    try {
      lock.writeLock().lock();
      //先调用任务的registered方法，让任务初始化
      taskDetail.registered(this);
      addTask(taskDetail);
    } finally {
      lock.writeLock().unlock();
    }
  }

  /**
   * 向任务队列中添加任务。
   *
   * @param taskDetail 待添加的任务详情
   */
  public void addTask(TaskDetail taskDetail) {
    try {
      lock.writeLock().lock();
      Long nextedTime = taskDetail.getNextTime();
      if (nextedTime == null) {
        return;
      }
      // 将任务按照执行时间插入到正确的位置，保持队列的有序性
      int index = 0;
      for (TaskDetail detail : taskQueue) {
        if (detail.getNextTime() > nextedTime) {
          break;
        }
        index++;
      }
      taskQueue.add(index, taskDetail);
    } finally {
      lock.writeLock().unlock();
    }
  }

  /**
   * 启动任务调度器，开始按照预定间隔执行任务。
   */
  public void start() {
    // 仅当启动标志为false时，才执行启动逻辑，避免重复启动
    if (started.compareAndSet(false, true)) {
      executorService = Executors.newSingleThreadScheduledExecutor();
      // 每隔500毫秒检查是否有任务需要执行
      executorService.scheduleAtFixedRate(() -> {
        lock.writeLock().lock();
        try {
          while (!taskQueue.isEmpty()) {
            // 从队列中获取下一个需要执行的任务
            TaskDetail detail = taskQueue.peek();
            if (detail.getNextTime() > System.currentTimeMillis()) {
              return;
            }
            taskQueue.poll();
            // 执行任务
            executeTask(detail);
          }
        } catch (Exception e) {
          log.error(e.getMessage(), e);
        } finally {
          lock.writeLock().unlock();
        }
      }, 0, 500, java.util.concurrent.TimeUnit.MILLISECONDS);
    }
  }

  /**
   * 停止任务调度器，取消所有计划任务。
   */
  public void stop() {
    // 仅当启动标志为true时，才执行关闭逻辑，避免重复关闭
    if (started.compareAndSet(true, false)) {
      executorService.shutdown();
      try {
        if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
          executorService.shutdownNow(); // 如果等待超时，则取消所有仍在执行的任务
        }
      } catch (InterruptedException e) {
        executorService.shutdownNow(); // 如果当前线程在等待时被中断，则取消所有任务
        Thread.currentThread().interrupt(); // 重新设置中断状态
      }
    }
  }

  /**
   * 执行指定的任务。
   *
   * @param detail 待执行的任务详情
   */
  public void executeTask(TaskDetail detail) {
    taskExecutor.execute(detail);
  }

}

