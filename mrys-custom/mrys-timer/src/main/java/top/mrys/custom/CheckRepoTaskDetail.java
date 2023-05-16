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
    this.setNextTime(System.currentTimeMillis() + delay);
  }

  @Override
  public Long getTaskId() {
    return 0L;
  }


  @Override
  public Task getTask() {
    return param -> {
      TaskScheduler scheduler = param.getScheduler();
      taskRepo.takeWaitingTask().forEach(scheduler::addTask);
      TaskRet ret = new TaskRet();
      ret.setNewTaskDetail(new CheckRepoTaskDetail(delay, taskRepo));
      return ret;
    };
  }

  @Override
  public GenNextTime getGenNextTime() {
    return () -> System.currentTimeMillis() + delay;
  }
}
