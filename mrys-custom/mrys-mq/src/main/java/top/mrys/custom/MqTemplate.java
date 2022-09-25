package top.mrys.custom;

import java.util.concurrent.TimeUnit;

/**
 * @author mrys
 */
public interface MqTemplate {

  <T> void send(String routingKey, T message);

  <T> void sendDelay(String routingKey, T message, Long delayTime, TimeUnit timeUnit);
}
