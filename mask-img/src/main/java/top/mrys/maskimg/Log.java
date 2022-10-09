package top.mrys.maskimg;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Log {
  private static List<Consumer<String>> consumers = new ArrayList<>();
  private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  public static void setConsumer(Consumer<String> consumer) {
    Log.consumers.add(consumer);
  }

  public static void info(String msg) {
    if (!consumers.isEmpty()) {
      consumers.forEach(consumer -> consumer.accept(LocalDateTime.now().format(formatter) + " info " + msg + "\n"));
    }
  }
}
