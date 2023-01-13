package top.mrys.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.mrys.custom.filters.customizers.RuleCustomizer;
import top.mrys.custom.filters.rules.PathRule;
import top.mrys.custom.filters.rules.RateLimiterRule;

/**
 * @author mrys
 */
@Configuration(proxyBeanMethods = false)
public class SecurityConfig {

  @Bean
  public RuleCustomizer ruleCustomizer() {
    return rule -> rule.add(new PathRule("/user1")
      .rule(new RateLimiterRule("/user1", 1)));
  }
}
