package top.mrys.custom.schema;

import io.swagger.v3.oas.models.media.Schema;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * @author mrys
 */
public record SchemaMetadata(Type type, String name, String description, Map<String, Schema<?>> schemaMap) {

}
