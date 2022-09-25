package top.mrys.custom;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.concurrent.TimeUnit;

public class RabbitMqTemplate implements MqTemplate {

  private final RabbitTemplate rabbitTemplate;

  private final String exchange;

  private final String delayExchange;

  public RabbitMqTemplate(RabbitTemplate rabbitTemplate, String exchange, String delayExchange) {
    this.rabbitTemplate = rabbitTemplate;
    this.exchange = exchange;
    this.delayExchange = delayExchange;
  }

  @Override
  public <T> void send(String routingKey, T message) {
    rabbitTemplate.convertAndSend(routingKey, message);
  }

  @Override
  public <T> void sendDelay(String routingKey, T message, Long delayTime, TimeUnit timeUnit) {
    send(delayExchange, routingKey, message, delayTime, timeUnit);
  }

  protected <T> void send(String exchange, String routingKey, T message, Long delayTime, TimeUnit timeUnit) {
    rabbitTemplate.convertAndSend(exchange, routingKey, message, message1 -> {
      message1.getMessageProperties().setHeader("x-delay", timeUnit.toMillis(delayTime));
      return message1;
    });
  }
}
