package top.mrys.custom.config;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import jakarta.servlet.Filter;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;
import top.mrys.core.Result;
import top.mrys.core.ResultException;
import top.mrys.custom.annotations.Anno;
import top.mrys.custom.annotations.Auth;
import top.mrys.custom.annotations.AuthAlias;
import top.mrys.custom.core.*;
import top.mrys.custom.exceptions.AuthenticationException;
import top.mrys.custom.exceptions.NoLoginException;
import top.mrys.custom.exceptions.handlers.ExceptionHandlerRegistry;
import top.mrys.custom.login.functions.LoginFunctionRedirect;
import top.mrys.custom.login.functions.LoginFunctionResult;
import top.mrys.custom.mvc.HandlerFunctionRequest;
import top.mrys.custom.mvc.MvcRequest;
import top.mrys.custom.mvc.MvcResponse;
import top.mrys.custom.mvc.MvcServerExchange;

import java.io.IOException;
import java.lang.reflect.AnnotatedElement;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.web.servlet.function.RequestPredicates.POST;

/**
 * spring mvc 配置
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@AutoConfigureAfter(AutoConfigurationSecurity.class)
@Slf4j
public class WebMvcConfig implements Filter, WebMvcConfigurer {
  @Autowired
  private List<SecurityFilter> filters;
  @Autowired
  private SecurityContext securityContext;
  @Autowired
  private ApplicationContext applicationContext;
  @Autowired
  private Optional<List<LoginFunction>> loginFunctionsOptional;
  @Autowired
  private ExceptionHandlerRegistry exceptionHandlerRegistry;

  @Override
  @SneakyThrows
  public void doFilter(ServletRequest request, ServletResponse response, jakarta.servlet.FilterChain chain) throws IOException, ServletException {
    log.debug("构建权限过滤器");
    ArrayList<SecurityFilter> list = new ArrayList<>(filters);
    list.add((exchange, chain1) -> {
      try {
        chain.doFilter((ServletRequest) exchange.getRequest().getNativeRequest(), (ServletResponse) exchange.getResponse().getNativeResponse());
      } catch (Throwable e) {
        if (e instanceof ResultException re) {
          log.warn(e.getMessage());
        }
        if (log.isDebugEnabled()) {
          log.error(e.getMessage(), e);
        }
        throw e;
      }
    });
    //构建过滤器链
    FilterChain filterChain = new FilterChain(list);
    //过滤器链异常处理
    filterChain.setExceptionHandlerRegistry(exceptionHandlerRegistry);

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
        if (AuthTool.isSuperAdmin()) {
          //超级管理员 不需要判断权限
          return true;
        }
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
          boolean login = AuthTool.isLogin();
          if (login) {
            log.trace("已登录");
          } else {
            log.trace("未登录");
            throw new NoLoginException();
          }
          return login;
        }
        return true;
      }
    });
  }

  /**
   * 用户登录路由
   * 登录也可以用过滤器来实现
   */
  @Bean
  public RouterFunction<ServerResponse> loginRouterFunction() {
    return RouterFunctions.route(POST("/auth/login/{type}"), request -> {
        String type = request.pathVariable("type");
        HandlerFunctionRequest functionRequest = new HandlerFunctionRequest(request);
        if (loginFunctionsOptional.isEmpty()) {
          //没有登录函数
          return ServerResponse.status(HttpStatus.NOT_FOUND).build();
        }
        List<LoginFunction> loginFunctions = loginFunctionsOptional.get();
        if (CollUtil.isEmpty(loginFunctions)) {
          throw new RuntimeException("没有登录实现");
        }
        LoginFunction loginFunction = loginFunctions.stream()
          .filter(lf -> lf.support(type))
          .findFirst()//找到第一个
          .orElseThrow(() -> new RuntimeException(String.format("没有登录实现%s", type)));
        Authentication authentication = loginFunction.login(functionRequest);

        if (authentication != null && authentication.isAuthenticated()) {
          //todo 登录成功
          //登录成功
          SecurityContextHolder.getContext().setAuthentication(authentication);
          //返回json
          if (loginFunction instanceof LoginFunctionResult result) {
            return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
              .body(result.getResult(authentication));
          }
          //重定向
          if (loginFunction instanceof LoginFunctionRedirect redirect) {
            return ServerResponse.temporaryRedirect(URI.create(redirect.getRedirectUrl(authentication))).build();
          }
        }
        //todo 登录失败

        return ServerResponse.ok()
          .contentType(MediaType.APPLICATION_JSON)
          .body(JSONUtil.toJsonStr(Result.fail("登录失败")));
      }
    );
  }

  /**
   * 验证是否有权限
   */
  private static boolean checkAuth(AnnotatedElement annotatedElement, StandardEvaluationContext context, ExpressionParser parser, TemplateParserContext templateParserContext) {
    if (!AuthTool.isLogin()) {
      throw NoLoginException.getInstance();
    }
    Auth auth = AnnotatedElementUtils.findMergedAnnotation(annotatedElement, Auth.class);
    if (AnnotationUtils.isSynthesizedAnnotation(auth)) {
      AuthAlias alias = AnnotatedElementUtils.findMergedAnnotation(annotatedElement, AuthAlias.class);
      context.setVariable("alias", AnnotatedElementUtils.findMergedAnnotation(annotatedElement, alias.value()));
    }
    if (Boolean.FALSE.equals(parser.parseExpression(auth.value(), templateParserContext).getValue(context, Boolean.class))) {
      throw new AuthenticationException(parser.parseExpression(auth.msg(), templateParserContext).getValue(context, String.class));
    }
    return true;
  }
}
