package top.mrys.custom.schema;

import io.swagger.v3.oas.models.media.Schema;

/**
 * @author mrys
 */
public interface SchemaTypeHandler {

  Schema<?> handle(SchemaMetadata metadata);
}
