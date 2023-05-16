package top.mrys.custom;

import lombok.Getter;
import lombok.Setter;

/**
 * @author mrys
 */
public abstract class BaseTaskDetail implements TaskDetail {

  private TaskScheduler scheduler;

  protected long nextTime;

  @Setter
  @Getter
  private Long taskId;

  @Setter
  @Getter
  private String taskCode;

  @Setter
  @Getter
  private String taskName;

  @Setter
  @Getter
  private String taskParam;

  @Setter
  @Getter
  private Long execTimes;

  @Setter
  @Getter
  private Long needTimes;

  @Override
  public void setScheduler(TaskScheduler scheduler) {
    this.scheduler = scheduler;
  }

  @Override
  public TaskScheduler getScheduler() {
    return scheduler;
  }

  @Override
  public Long getNextTime() {
    return nextTime;
  }
}
