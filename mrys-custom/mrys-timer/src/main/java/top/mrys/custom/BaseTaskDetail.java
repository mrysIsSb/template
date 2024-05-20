package top.mrys.custom;

import lombok.Getter;
import lombok.Setter;

/**
 * @author mrys
 */
public abstract class BaseTaskDetail<P> implements TaskDetail {

  @Getter
  private TaskScheduler scheduler;

  @Setter
  @Getter
  private Long nextTime;

  @Setter
  @Getter
  private String name;

  @Setter
  @Getter
  private P taskParam;

  @Setter
  @Getter
  private Long execTimes;


  @Override
  public void register(TaskScheduler scheduler) {
    if (this.scheduler != null) {
      throw new RuntimeException("任务已经注册异常");
    }
    this.scheduler = scheduler;
    this.scheduler.addTask(this);
  }

}
