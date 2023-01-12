package top.mrys.custom.customizers;


import io.swagger.v3.oas.models.Paths;

/**
 * @author mrys
 */
public interface ApiPathsCustomizer {

  void customize(Paths paths);
}
