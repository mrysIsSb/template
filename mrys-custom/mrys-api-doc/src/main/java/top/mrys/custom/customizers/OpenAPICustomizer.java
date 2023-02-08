package top.mrys.custom.customizers;

import io.swagger.v3.oas.models.OpenAPI;

/**
 * 自定义openapi
 * @author mrys
 */
public interface OpenAPICustomizer {

  void customize(OpenAPI openAPI);
}
