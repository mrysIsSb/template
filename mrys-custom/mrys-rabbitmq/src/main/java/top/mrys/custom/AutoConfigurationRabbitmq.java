package top.mrys.custom;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.CustomExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ConditionalOnBean(RabbitTemplate.class)
public class AutoConfigurationRabbitmq {

  public static final String DELAY_EXCHANGE = "delay_exchange";

  public static final String DELAY_QUEUE = "delay_queue";

  public static final String DELAY_ROUTING_KEY = "delay_routing_key";

  /**
   * 死信交换机
   */
  @Bean(name = DELAY_EXCHANGE)
  public CustomExchange delayExchange() {
    return new CustomExchange(DELAY_EXCHANGE, "x-delayed-message", true, false);
  }

  /**
   * 死信队列
   */
  @Bean(name = DELAY_QUEUE)
  public Queue delayQueue() {
    return new Queue(DELAY_QUEUE, true);
  }

  /**
   * 死信队列绑定
   */
  @Bean
  public Binding binding() {
    return new Binding(DELAY_QUEUE, Binding.DestinationType.QUEUE, DELAY_EXCHANGE, DELAY_ROUTING_KEY, null);
  }

  @Bean
  public RabbitMqTemplateFactory rabbitMqTemplateFactory(RabbitTemplate rabbitTemplate) {
    return new RabbitMqTemplateFactory(rabbitTemplate, DELAY_EXCHANGE);
  }


}
