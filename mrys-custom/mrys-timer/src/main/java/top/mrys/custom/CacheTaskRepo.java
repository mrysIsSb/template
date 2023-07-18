package top.mrys.custom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 任务仓库
 * <p>
 * 只支持单体服务
 *
 * @author mrys
 */
public class CacheTaskRepo implements TaskRepo {

  private final TaskRepo delegate;

  private Long lastTime;

  /**
   * 任务队列
   */
  private List<TaskDetail> queue = new ArrayList<>();

  /**
   * 缓存时间跨度（ms）
   */
  private final Long cacheTimeSpan;

  private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();


  public CacheTaskRepo(TaskRepo delegate, Long cacheTimeSpan) {
    this.delegate = delegate;
    this.cacheTimeSpan = cacheTimeSpan;
  }

  @Override
  public void addTask(TaskDetail taskDetail) {
    try {
      lock.writeLock().lock();
      delegate.addTask(taskDetail);
      if (taskDetail.getNextTime() <= lastTime) {
        queue.add(taskDetail);
        queue.sort((o1, o2) -> (int) (o1.getNextTime() - o2.getNextTime()));
      }
    } finally {
      lock.writeLock().unlock();
    }
  }

  @Override
  public void removeTask(TaskDetail taskParam) {
    try {
      lock.writeLock().lock();
      delegate.removeTask(taskParam);
      queue.removeIf(taskDetail -> taskDetail.getTaskId().equals(taskParam.getTaskId()));
    } finally {
      lock.writeLock().unlock();
    }
  }

  /**
   * 获取等待执行的任务
   * <p>
   *
   * @param execTime 执行时间
   * @return
   */
  @Override
  public List<TaskDetail> takeWaitingTask(Long execTime) {
    if (lastTime == null || lastTime < execTime) {
      lastTime = execTime + cacheTimeSpan;
      List<TaskDetail> taskDetails = delegate.takeWaitingTask(lastTime);
      if (taskDetails != null && taskDetails.size() > 0) {
        taskDetails.sort((o1, o2) -> (int) (o1.getNextTime() - o2.getNeedTimes()));
        lock.writeLock().lock();
        try {
          if (queue.size() == 0) {
            queue.addAll(taskDetails);
          }
          int index = 0;
          for (TaskDetail detail : taskDetails) {
            for (; index < queue.size(); index++) {
              TaskDetail taskDetail = queue.get(index);
              if (taskDetail.getNextTime() > detail.getNextTime()) {
                queue.add(index, detail);
                index++;
                break;
              }
            }
          }
        } finally {
          lock.writeLock().unlock();
        }
      }
    }
    //-----
    try {
      lock.writeLock().lock();
      int from = 0;
      for (int i = 0; i < queue.size(); i++) {
        TaskDetail taskDetail = queue.get(i);
        if (taskDetail.getNextTime() <= execTime) {
          from = i + 1;
        } else {
          break;
        }
      }

      if (from > 0) {
        List<TaskDetail> taskDetails = queue.subList(0, from);
        queue = queue.subList(from, queue.size());
        return taskDetails;
      }

    } finally {
      lock.writeLock().unlock();
    }
    return Collections.emptyList();
  }

  @Override
  public void init() {
    delegate.init();
  }
}
