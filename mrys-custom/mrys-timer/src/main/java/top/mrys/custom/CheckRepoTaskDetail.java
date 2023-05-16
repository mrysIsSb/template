package top.mrys.custom;

/**
 * @author mrys
 */
public class CheckRepoTaskDetail extends BaseTaskDetail {

  private final long delay;
  private final TaskRepo taskRepo;


  public CheckRepoTaskDetail(long delay, TaskRepo taskRepo) {
    this.delay = delay;
    this.taskRepo = taskRepo;
    this.nextTime = System.currentTimeMillis() + delay;
  }

  @Override
  public Long getTaskId() {
    return 0L;
  }

  @Override
  public String getTaskParam() {
    return null;
  }

  @Override
  public Task getTask() {
    return param -> {
      TaskScheduler scheduler = param.getScheduler();
      taskRepo.getWaitingTask().forEach(scheduler::addTask);
      TaskRet ret = new TaskRet();
      ret.setNewTaskDetail(new CheckRepoTaskDetail(delay, taskRepo));
      return ret;
    };
  }
}
