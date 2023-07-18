package top.mrys.custom;

/**
 * @author mrys
 */

public class TestScheduler {

//  @Test
//  public void test1() {
//    TaskScheduler scheduler = new TaskScheduler(new TestTaskRepo(), new SimpleTaskExecutor(Executors.newSingleThreadExecutor(), task -> {
//      System.out.println("代理");
//      return new TaskProxy(task) {
//        @Override
//        public TaskRet execute(TaskDetail param) {
//          System.out.println("代理执行");
//          TaskRet ret = super.execute(param);
//          System.out.println("代理执行完毕");
//          return ret;
//        }
//      };
//    }));
//    scheduler.start();
//    scheduler.addTask(new TestTaskDetail(System.currentTimeMillis() + 1000 * 10));
//    while (true) {
//    }
//  }
}
