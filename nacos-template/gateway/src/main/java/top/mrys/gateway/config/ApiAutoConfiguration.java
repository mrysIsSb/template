package top.mrys.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import top.mrys.gateway.config.swagger.SwaggerProvider;

/**
 * @author mrys
 */
@Configuration
@Profile({"dev", "test"})
public class ApiAutoConfiguration {


  @Bean
  public RouterFunction<ServerResponse> swaggerRouterFunction(SwaggerProvider swaggerProvider) {
    return RouterFunctions
      .route(RequestPredicates.GET("/swagger-resources")
          .and(RequestPredicates.accept(MediaType.ALL)),
        request -> ServerResponse.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON)
          .body(BodyInserters.fromValue(swaggerProvider.get())))

      .andRoute(RequestPredicates.GET("/swagger-resources/configuration/ui")
          .and(RequestPredicates.accept(MediaType.ALL)),
        request -> ServerResponse.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON)
          .body(BodyInserters.fromValue("{}")))

      .andRoute(RequestPredicates.GET("/swagger-resources/configuration/security")
          .and(RequestPredicates.accept(MediaType.ALL)),
        request -> ServerResponse.status(HttpStatus.OK).contentType(MediaType.APPLICATION_JSON)
          .body(BodyInserters.fromValue("{}")));
  }

}
