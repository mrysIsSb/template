package top.mrys.base.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * @author mrys
 */
@Configuration(proxyBeanMethods = false)
public class RestTemplateAutoConfiguration {

  @Bean
  @LoadBalanced //开启负载均衡 就是加个了拦截器
  public RestTemplate restTemplate(){
    return new RestTemplate();
  }

}
