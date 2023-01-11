package top.mrys.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;
import top.mrys.custom.EnableApiDoc;

/**
 * @author mrys
 */
@EnableApiDoc
@Configuration(proxyBeanMethods = false)
public class ApiDocConfig {

 @Bean
//  public RouterFunctionDsl
  public RouterFunction<ServerResponse> routerFunction() {
    return RouterFunctions
      .route()
      .GET("/v3/api-doc", request -> ServerResponse.ok().body("hello"))
      .build();
  }
}