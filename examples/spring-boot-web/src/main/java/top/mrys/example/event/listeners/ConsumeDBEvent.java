package top.mrys.example.event.listeners;

/**
 * @author mrys
 */
public interface ConsumeDBEvent<T> {

  boolean support(Class<T> clazz);

  void consumeInsert(T t);

  void consumeUpdate(T t, T old);

}
