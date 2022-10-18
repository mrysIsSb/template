package top.mrys;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.util.StatusPrinter;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.concurrent.TimeUnit;

/**
 * Hello world!
 */
public class App {

  //log
  private static final Logger log = LoggerFactory.getLogger(App.class);

  public static void main(String[] args) {
    ILoggerFactory factory = LoggerFactory.getILoggerFactory();
    //print internal state
    StatusPrinter.print((Context) factory);

    MDC.put("traceId", "123");
    while (true) {
      log.info("hello world");
      try {
        TimeUnit.SECONDS.sleep(1);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}
