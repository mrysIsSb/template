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
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import reactor.core.publisher.Mono;
import top.mrys.custom.annotations.Anno;
import top.mrys.custom.annotations.Auth;
import top.mrys.custom.annotations.AuthAlias;
import top.mrys.custom.exceptions.AuthenticationException;
import top.mrys.custom.exceptions.NoLoginException;
import top.mrys.custom.filters.*;
import top.mrys.custom.filters.authenticate.PropertiesTokenAuthenticateProvider;
import top.mrys.custom.mvc.MvcRequest;
import top.mrys.custom.mvc.MvcResponse;
import top.mrys.custom.mvc.MvcServerExchange;

import java.io.IOException;
import java.lang.reflect.AnnotatedElement;
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

//  @Bean
//  public AnnotationFilter annotationFilter() {
//    return new AnnotationFilter(handlerMapping);
//  }

  @Bean
  @ConditionalOnProperty(prefix = "security.local", name = "enable", havingValue = "true")
  public PropertiesTokenAuthenticateProvider propertiesTokenAuthenticateProvider(SecurityProperties securityProperties) {
    return new PropertiesTokenAuthenticateProvider(securityProperties.getLocal().getUsers());
  }


  @Configuration(proxyBeanMethods = false)
  @ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
  public static class WebMvcConfig implements Filter, WebMvcConfigurer {

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
        } catch (RuntimeException e) {
            throw e;
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

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

      ExpressionParser parser = new SpelExpressionParser();

      TemplateParserContext templateParserContext = new TemplateParserContext();
      registry.addInterceptor(new HandlerInterceptor() {
        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
          if (handler instanceof HandlerMethod handlerMethod) {
            StandardEvaluationContext context = new StandardEvaluationContext();
            context.setBeanResolver(new BeanFactoryResolver(applicationContext));
            if (handlerMethod.hasMethodAnnotation(Anno.class)) {
              log.trace("跳过拦截");
              return true;
            }
            if (handlerMethod.hasMethodAnnotation(Auth.class)) {
              log.trace("需要拦截");
              return checkAuth(handlerMethod.getMethod(), context, parser, templateParserContext);
            }
            if (handlerMethod.getBeanType().isAnnotationPresent(Anno.class)) {
              log.trace("跳过拦截");
              return true;
            }
            if (handlerMethod.getBeanType().isAnnotationPresent(Auth.class)) {
              log.trace("需要拦截");
              return checkAuth(handlerMethod.getBeanType(), context, parser, templateParserContext);
            }
            return AuthTool.isLogin();
          }
          return true;
        }
      });
    }

    private static boolean checkAuth(AnnotatedElement annotatedElement, StandardEvaluationContext context, ExpressionParser parser, TemplateParserContext templateParserContext) {
      if (!AuthTool.isLogin()) {
        throw NoLoginException.getInstance();
      }
      Auth auth = AnnotatedElementUtils.findMergedAnnotation(annotatedElement,Auth.class);
      if (AnnotationUtils.isSynthesizedAnnotation(auth)) {
        AuthAlias alias = AnnotatedElementUtils.findMergedAnnotation(annotatedElement,AuthAlias.class);
        context.setVariable("alias", AnnotatedElementUtils.findMergedAnnotation(annotatedElement,alias.value()));
      }
      if (Boolean.FALSE.equals(parser.parseExpression(auth.value(), templateParserContext).getValue(context, Boolean.class))){
        throw new AuthenticationException(parser.parseExpression(auth.msg(), templateParserContext).getValue(context, String.class));
      }
      return true;
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
