package top.mrys.custom;

import java.util.Optional;
import java.util.concurrent.Executor;
import java.util.function.Function;

/**
 * 任务执行器
 *
 * @author mrys
 */
public class SimpleTaskExecutor implements TaskExecutor {

  private Executor executor;

  private Function<Task, Task> taskProxy = Function.identity();

  public SimpleTaskExecutor(Executor executor) {
    this.executor = executor;
  }

  public SimpleTaskExecutor(Executor executor, Function<Task, Task> taskProxy) {
    this.executor = executor;
    this.taskProxy = taskProxy;
  }

  @Override
  public void execute(TaskDetail taskDetail) {
    executor.execute(() -> {
      Task task = taskProxy.apply(taskDetail.getTask());
      Long times = taskDetail.getExecTimes() + 1;
      TaskRepo repo = taskDetail.getScheduler().getTaskRepo();
      taskDetail.setTaskStatus(2);
      repo.addTask(taskDetail);
      TaskRet ret = task.execute(taskDetail);
      //执行成功
      if (ret == null) {
        return;
      }
      Optional<TaskDetail> newTaskDetail = ret.getNewTaskDetail();
      if (newTaskDetail.isEmpty()) {
        //没有新任务 自动建一个
        taskDetail.setExecTimes(times);
        if (taskDetail.getGenNextTime() != null) {
          taskDetail.setNextTime(taskDetail.getGenNextTime().getNextTime());
        }
        if (taskDetail.getNeedTimes() > 0 && times >= taskDetail.getNeedTimes()) {
          taskDetail.setTaskStatus(3);//结束了
        }
        newTaskDetail = Optional.of(taskDetail);
      }
      newTaskDetail.ifPresent(newDetail -> taskDetail.getScheduler().addTask(newDetail));
    });
  }
}
