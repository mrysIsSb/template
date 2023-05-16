package top.mrys.custom;

import lombok.Getter;

/**
 * @author mrys
 */
public abstract class TaskProxy implements Task {

  @Getter
  private final Task task;

  public TaskProxy(Task task) {
    this.task = task;
  }

  @Override
  public TaskRet execute(TaskDetail param) {
    return task.execute(param);
  }
}
