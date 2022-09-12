package top.mrys.gateway.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

/**
 * webflux 网关 swagger 资源路径配置
 * /swagger-ui/index.html
 * /doc.html
 * @author mrys
 */
@Configuration
public class WebFluxSwaggerConfiguration implements WebFluxConfigurer {

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    registry.addResourceHandler("/swagger-ui/**")
      .addResourceLocations("classpath:/META-INF/resources/webjars/springfox-swagger-ui/")
      .resourceChain(false);
  }

}