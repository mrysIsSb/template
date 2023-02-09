package top.mrys.custom.customizers;

import io.swagger.v3.oas.models.security.SecurityScheme;

import java.util.Map;

/**
 * @author mrys
 */
public interface ApiSecuritySchemesCustomizer {

  void customize(Map<String, SecurityScheme> securitySchemes);
}
