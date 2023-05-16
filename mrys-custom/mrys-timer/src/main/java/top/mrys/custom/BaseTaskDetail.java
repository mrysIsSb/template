package top.mrys.custom;

/**
 * @author mrys
 */
public abstract class BaseTaskDetail implements TaskDetail {

  private TaskScheduler scheduler;

  protected long nextTime;

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
