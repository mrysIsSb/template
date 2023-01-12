package top.mrys.custom.customizers;

import io.swagger.v3.oas.models.media.Schema;

import java.util.Map;

/**
 * @author mrys
 */
public interface ApiSchemaCustomizer {

    void customize(Map<String, Schema> schemas);
}
