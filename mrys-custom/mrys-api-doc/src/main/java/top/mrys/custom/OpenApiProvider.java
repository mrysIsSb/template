package top.mrys.custom;

import io.swagger.v3.oas.models.OpenAPI;

/**
 * @author mrys
 */
public interface OpenApiProvider {

  /**
   * 获取openApi
   */
  OpenAPI getOpenApi();

}
