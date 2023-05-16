package top.mrys.custom;

import java.util.Date;

/**
 * @author mrys
 */
public class TestTaskDetail extends BaseTaskDetail {

  public TestTaskDetail(long nextTime) {
    setNextTime(nextTime);
  }

  @Override
  public Long getTaskId() {
    return null;
  }

  @Override
  public String getTaskParam() {
    return null;
  }

  @Override
  public Task getTask() {
    return new Task() {
      @Override
      public TaskRet execute(TaskDetail param) {
        System.out.println(new Date()+"执行任务--");
        TaskRet ret = new TaskRet();
        ret.setNewTaskDetail(new TestTaskDetail(System.currentTimeMillis()+1000*10));
        return ret;
      }
    };
  }

  @Override
  public GenNextTime getGenNextTime() {
    return null;
  }

}
