package top.mrys.custom;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import top.mrys.custom.filters.*;
import top.mrys.custom.mvc.MvcRequest;
import top.mrys.custom.mvc.MvcResponse;
import top.mrys.custom.mvc.MvcServerExchange;

import java.util.List;

@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication
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

  @Configuration(proxyBeanMethods = false)
  @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
  public static class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private List<SecurityFilter> filters;

    @Autowired
    private SecurityContext securityContext;

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
      log.info("mvc 添加权限拦截器");
      // 将权限过滤器添加到拦截器
      registry.addInterceptor(new HandlerInterceptor() {
        @Override
        public boolean preHandle(jakarta.servlet.http.HttpServletRequest request, jakarta.servlet.http.HttpServletResponse response, Object handler) throws Exception {
          FilterChain chain = new FilterChain(filters);
          MvcRequest mvcRequest = new MvcRequest(request);

          MvcResponse mvcResponse = new MvcResponse(response);

          SpringInstanceProvider instanceProvider = new SpringInstanceProvider(applicationContext);

          ServerExchange exchange = new MvcServerExchange(mvcRequest, mvcResponse, securityContext, instanceProvider);
          chain.doFilter(exchange);
          return true;
        }
      });
    }
  }

//TODO 2022年9月27日 支持webflux
}
