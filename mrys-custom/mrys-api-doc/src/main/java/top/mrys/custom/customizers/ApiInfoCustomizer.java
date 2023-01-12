package top.mrys.custom.customizers;


import io.swagger.v3.oas.models.info.Info;

/**
 * @author mrys
 */
public interface ApiInfoCustomizer {

  void customize(Info info);
}
