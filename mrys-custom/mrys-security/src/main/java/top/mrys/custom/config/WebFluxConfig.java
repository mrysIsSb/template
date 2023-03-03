package top.mrys.custom.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import top.mrys.custom.core.FilterChain;
import top.mrys.custom.core.SecurityContext;
import top.mrys.custom.core.SecurityFilter;

import java.util.List;

/**
 * TODO 2022年9月27日 支持webflux
 * @author mrys
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class WebFluxConfig implements WebFilter {

  @Autowired
  private List<SecurityFilter> filters;

  @Autowired
  private SecurityContext securityContext;

  @Autowired
  private ApplicationContext applicationContext;

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    log.debug("构建flux权限过滤器");
    FilterChain filterChain = new FilterChain(filters);
    chain.filter(exchange);
    return null;
  }

}
