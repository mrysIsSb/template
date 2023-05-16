package top.mrys.custom;

import lombok.Setter;

import java.util.Optional;

/**
 * @author mrys
 */
public class TaskRet {

  @Setter
  private TaskDetail newTaskDetail;

  public Optional<TaskDetail> getNewTaskDetail() {
    return Optional.ofNullable(newTaskDetail);
  }
}
