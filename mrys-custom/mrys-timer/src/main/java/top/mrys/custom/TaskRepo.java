package top.mrys.custom;

import java.util.List;

/**
 * 任务仓库
 *
 * @author mrys
 */
public interface TaskRepo {

  /**
   * 添加任务
   *
   * @param taskDetail
   */
  void addTask(TaskDetail taskDetail);

  /**
   * 删除任务
   *
   * @param taskParam
   */
  void removeTask(TaskDetail taskDetail);

  /**
   * 获取等待执行的任务
   *
   * @param execTime 执行时间
   * @return 任务列表
   */
  List<TaskDetail> takeWaitingTask(Long execTime);

  /**
   * 初始化
   */
  void init();
}
