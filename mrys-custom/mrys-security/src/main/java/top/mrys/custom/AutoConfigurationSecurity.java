package top.mrys.custom;

import jakarta.servlet.Filter;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import top.mrys.custom.filters.*;
import top.mrys.custom.filters.authenticate.PropertiesTokenAuthenticateProvider;
import top.mrys.custom.mvc.MvcRequest;
import top.mrys.custom.mvc.MvcResponse;
import top.mrys.custom.mvc.MvcServerExchange;

import java.io.IOException;
import java.util.List;

@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication
@EnableConfigurationProperties(SecurityProperties.class)
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
  @ConditionalOnProperty(prefix = "security.local", name = "enable", havingValue = "true")
  public PropertiesTokenAuthenticateProvider propertiesTokenAuthenticateProvider(SecurityProperties securityProperties) {
    return new PropertiesTokenAuthenticateProvider(securityProperties.getLocal().getUsers());
  }


  @Configuration(proxyBeanMethods = false)
  @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
  public static class WebMvcConfig implements Filter {

    @Autowired
    private List<SecurityFilter> filters;

    @Autowired
    private SecurityContext securityContext;

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, jakarta.servlet.FilterChain chain) throws IOException, ServletException {
      log.debug("构建权限过滤器");
        filters.add((exchange, chain1) -> {
            try {
                chain.doFilter(request, response);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
      top.mrys.custom.FilterChain filterChain = new top.mrys.custom.FilterChain(filters);

    MvcRequest mvcRequest = new MvcRequest((HttpServletRequest) request);

    MvcResponse mvcResponse = new MvcResponse((HttpServletResponse) response);

    SpringInstanceProvider instanceProvider = new SpringInstanceProvider(applicationContext);

    ServerExchange exchange = new MvcServerExchange(mvcRequest, mvcResponse, securityContext, instanceProvider);
    filterChain.doFilter(exchange);
    }
  }
//TODO 2022年9月27日 支持webflux

  @Configuration(proxyBeanMethods = false)
  @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
  public static class WebFluxConfig implements WebFilter {

    @Autowired
    private List<SecurityFilter> filters;

    @Autowired
    private SecurityContext securityContext;

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
      log.debug("构建flux权限过滤器");
      top.mrys.custom.FilterChain filterChain = new top.mrys.custom.FilterChain(filters);
      chain.filter(exchange);
      return null;
    }

  }
}
