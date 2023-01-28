package top.mrys.example.config;

import cn.hutool.json.JSONUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;
import top.mrys.custom.EnableApiDoc;
import top.mrys.custom.OpenApiProvider;

/**
 * @author mrys
 */
@EnableApiDoc
@Configuration(proxyBeanMethods = false)
public class ApiDocConfig {

  //  @Bean
//  public ApiInfoCustomizer apiInfoCustomizer() {
//    return info -> info.title("mrys-web").version("1.0");
//  }
  @Bean
  public RouterFunction<ServerResponse> routerFunction(OpenApiProvider openApiProvider) {
    return RouterFunctions
      .route()
      .GET("/v3/api-doc", request -> ServerResponse.ok().body(JSONUtil.toJsonStr(openApiProvider.getOpenApi())))
      .build();
  }
}