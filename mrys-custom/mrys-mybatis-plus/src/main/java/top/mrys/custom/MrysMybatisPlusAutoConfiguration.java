package top.mrys.custom;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author mrys
 */
@Configuration(proxyBeanMethods = false)
public class MrysMybatisPlusAutoConfiguration {

  @Bean
  public DBFillNowTime dbFillNowTime() {
    return new DBFillNowTime();
  }
}
