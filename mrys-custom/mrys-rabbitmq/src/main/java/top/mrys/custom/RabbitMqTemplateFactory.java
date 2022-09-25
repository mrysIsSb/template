package top.mrys.custom;

import org.springframework.amqp.rabbit.core.RabbitTemplate;

public class RabbitMqTemplateFactory implements MqTemplateFactory<String> {

  private final RabbitTemplate rabbitTemplate;

  private final String delayExchange;

  public RabbitMqTemplateFactory(RabbitTemplate rabbitTemplate, String delayExchange) {
    this.rabbitTemplate = rabbitTemplate;
    this.delayExchange = delayExchange;
  }

  @Override
  public MqTemplate getMqTemplate(String exchange) {
    return new RabbitMqTemplate(rabbitTemplate, exchange, delayExchange);
  }
}
