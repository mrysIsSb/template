package top.mrys.custom;

import java.util.List;

/**
 * 任务仓库
 *
 * @author mrys
 */
public interface TaskRepo {

  //add
  void addTask(TaskDetail taskDetail);

  void removeTask(TaskDetail taskParam);

  //取出快要执行的任务
  List<TaskDetail> takeWaitingTask();
}
