package top.mrys.custom.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import top.mrys.custom.core.SecurityContext;
import top.mrys.custom.core.ThreadLocalSecurityContext;
import top.mrys.custom.exceptions.handlers.*;
import top.mrys.custom.filters.*;

import java.util.List;
import java.util.Optional;

@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication
@EnableConfigurationProperties(SecurityProperties.class)
@Import({WebMvcConfig.class, WebFluxConfig.class})
@Slf4j
public class AutoConfigurationSecurity {

  @Bean
  public SecurityContext securityContext() {
    return new ThreadLocalSecurityContext();
  }
  @Bean
  public SecurityContextFilter securityContextFilter() {
    return new SecurityContextFilter();
  }
  @Bean
  public AccessTokenProviderFilter accessTokenProviderFilter() {
    return new AccessTokenProviderFilter();
  }
  @Bean
  @ConditionalOnBean(AccessTokenProviderFilter.class)
  public AccessTokenProvider accessTokenProvider() {
    return exchange -> exchange.getRequest().getHeader("access-token");
  }
  @Bean
  public AccessTokenAuthenticateFilter accessTokenAuthenticateFilter() {
    return new AccessTokenAuthenticateFilter();
  }
  @Bean
  public RuleFilter ruleFilter() {
    return new RuleFilter();
  }
  @Bean
  public DefaultExceptionHandler exceptionHandler() {
    return new DefaultExceptionHandler();
  }
  @Bean
  public ServletExceptionHandler servletExceptionHandler() {
    return new ServletExceptionHandler();
  }
  @Bean
  public ResultExceptionHandler resultExceptionHandler() {
    return new ResultExceptionHandler();
  }

  @Bean
  public ExceptionHandlerRegistry exceptionHandlerRegistry(Optional<List<ExceptionHandler>> exceptionHandlers) {
    return exceptionHandlers
      .map(ExceptionHandlerRegistry::new)
      .orElseGet(ExceptionHandlerRegistry::new);
  }





}
