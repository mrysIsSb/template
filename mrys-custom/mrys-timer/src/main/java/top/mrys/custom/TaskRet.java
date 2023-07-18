package top.mrys.custom;

import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

/**
 * @author mrys
 */
public class TaskRet {

  /**
   * 是否继续执行
   */
  @Setter
  @Getter
  private boolean _continue = true;

  /**
   * 新任务
   */
  @Setter
  private TaskDetail newTaskDetail;

  public Optional<TaskDetail> getNewTaskDetail() {
    return Optional.ofNullable(newTaskDetail);
  }
}
