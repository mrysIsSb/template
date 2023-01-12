package top.mrys.custom;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import top.mrys.custom.customizers.*;

import java.util.*;

/**
 * @author mrys
 */
@Configuration(proxyBeanMethods = false)
@Import({WebMvcRequestHandlerProvider.class})
public class ApiDocAutoConfiguration {

  @Bean
  public OpenApiProvider openApiProvider(Optional<List<ApiInfoCustomizer>> apiInfoCustomizers,
                                         Optional<List<ApiServerCustomizer>> apiServerCustomizers,
                                         Optional<List<ApiPathsCustomizer>> apiPathsCustomizers,
                                         Optional<List<ApiTagsCustomizer>> apiTagsCustomizers,
                                         Optional<List<ApiSchemaCustomizer>> apiSchemaCustomizers) {
    return new OpenApiProvider() {
      @Override
      public OpenAPI getOpenApi() {
        OpenAPI api = new OpenAPI();
        //info
        Info info = new Info().title("mrys").version("1.0");
        apiInfoCustomizers.ifPresent(customizers -> customizers.forEach(customizer -> customizer
          .customize(info)));
        api.info(info);
        //server
        List<Server> servers = Collections.emptyList();
        apiServerCustomizers.ifPresent(customizers -> customizers.forEach(customizer -> customizer
          .customize(servers)));
        api.servers(servers);
        //paths
        Paths paths = new Paths();
        apiPathsCustomizers.ifPresent(customizers -> customizers.forEach(customizer -> customizer
          .customize(paths)));
        api.paths(paths);
        //tags
        List<Tag> tags = Collections.emptyList();
        apiTagsCustomizers.ifPresent(customizers -> customizers.forEach(customizer -> customizer
          .customize(tags)));
        api.tags(tags);
//        api.paths(new Paths().addPathItem("/test", new PathItem().summary("tt测试").get(new Operation().summary("t测试").description("测试"))));
        api.path("/v3/api-doc", new PathItem().get(new Operation().requestBody(new RequestBody().description("请求体"))));
        Map<String,Schema> schemas = new HashMap<>();
        apiSchemaCustomizers.ifPresent(customizers -> customizers.forEach(customizer -> customizer
          .customize(schemas)));
        schemas.forEach(api::schema);
        return api;
      }
    };
  }
}
