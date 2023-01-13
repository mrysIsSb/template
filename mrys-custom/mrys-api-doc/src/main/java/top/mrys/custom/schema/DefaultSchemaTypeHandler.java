package top.mrys.custom.schema;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.models.media.*;
import org.springframework.core.annotation.AnnotatedElementUtils;
import top.mrys.core.Result;

import java.io.IOException;
import java.lang.reflect.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

/**
 * @author mrys
 */
public class DefaultSchemaTypeHandler implements SchemaTypeHandler {

  private static final Map<Type, SchemaTypeHandler> schemaTypeHandlerMap = new HashMap<>();
  public static final String COMPONENTS_SCHEMAS = "#/components/schemas/";

  static {
    schemaTypeHandlerMap.put(Map.class, metadata -> new MapSchema().name(metadata.name()).description(metadata.description()));
    schemaTypeHandlerMap.put(Object.class, metadata -> new ObjectSchema().name(metadata.name()).description(metadata.description()));
    schemaTypeHandlerMap.put(String.class, metadata -> new StringSchema().name(metadata.name()).description(metadata.description()));
    schemaTypeHandlerMap.put(Integer.class, metadata -> new IntegerSchema().name(metadata.name()).description(metadata.description()));
    schemaTypeHandlerMap.put(int.class, metadata -> new IntegerSchema().name(metadata.name()).description(metadata.description()));
    schemaTypeHandlerMap.put(Long.class, metadata -> new StringSchema().name(metadata.name()).format("int64").description(metadata.description()));
    schemaTypeHandlerMap.put(long.class, metadata -> new StringSchema().name(metadata.name()).format("int64").description(metadata.description()));
    schemaTypeHandlerMap.put(Boolean.class, metadata -> new BooleanSchema().name(metadata.name()).example(true).description(metadata.description()));
    schemaTypeHandlerMap.put(boolean.class, metadata -> new BooleanSchema().name(metadata.name()).example(true).description(metadata.description()));
    schemaTypeHandlerMap.put(Double.class, metadata -> new NumberSchema().name(metadata.name()).description(metadata.description()));
    schemaTypeHandlerMap.put(double.class, metadata -> new NumberSchema().name(metadata.name()).description(metadata.description()));
    schemaTypeHandlerMap.put(Float.class, metadata -> new NumberSchema().name(metadata.name()).description(metadata.description()));
    schemaTypeHandlerMap.put(float.class, metadata -> new NumberSchema().name(metadata.name()).description(metadata.description()));
    //date
    DateTime now = DateTime.now();
    schemaTypeHandlerMap.put(Date.class, metadata -> new StringSchema().format("date").name(metadata.name()).example(now.toString("yyyy-MM-dd HH:mm:ss")).description(metadata.description()));
    schemaTypeHandlerMap.put(java.sql.Date.class, metadata -> new StringSchema().format("date").name(metadata.name()).example(now.toString("yyyy-MM-dd HH:mm:ss")).description(metadata.description()));
    schemaTypeHandlerMap.put(java.sql.Timestamp.class, metadata -> new StringSchema().format("date").name(metadata.name()).example(now.toString("yyyy-MM-dd HH:mm:ss")).description(metadata.description()));
    schemaTypeHandlerMap.put(java.sql.Time.class, metadata -> new StringSchema().format("date").name(metadata.name()).example(now.toString("HH:mm:ss")).description(metadata.description()));
    schemaTypeHandlerMap.put(DateTime.class, metadata -> new StringSchema().format("date").name(metadata.name()).example(now.toString("yyyy-MM-dd HH:mm:ss")).description(metadata.description()));
    schemaTypeHandlerMap.put(LocalDateTime.class, metadata -> new StringSchema().format("date").name(metadata.name()).example(now.toString("yyyy-MM-dd HH:mm:ss")).description(metadata.description()));
    schemaTypeHandlerMap.put(LocalDate.class, metadata -> new StringSchema().format("date").name(metadata.name()).example(now.toString("yyyy-MM-dd")).description(metadata.description()));
    schemaTypeHandlerMap.put(LocalTime.class, metadata -> new StringSchema().format("date").name(metadata.name()).example(now.toString("HH:mm:ss")).description(metadata.description()));
    //enum
    schemaTypeHandlerMap.put(Enum.class, metadata -> new StringSchema().name(metadata.name()).description(metadata.description()));

  }

  @Override
  public Schema<?> handle(SchemaMetadata metadata) {
    SchemaTypeHandler handler = schemaTypeHandlerMap.get(metadata.type());
    if (handler != null) {
      return handler.handle(metadata);
    }

    if (metadata.type() instanceof ParameterizedType parameterizedType) {
      Type rawType = parameterizedType.getRawType();
      if (rawType instanceof Class<?> aClass) {
        if (Collection.class.isAssignableFrom(aClass)) {
          Type[] typeArguments = parameterizedType.getActualTypeArguments();
          if (typeArguments.length == 1) {
            Type typeArgument = typeArguments[0];
            Schema<?> schema = handle(new SchemaMetadata(typeArgument, "", "", metadata.schemaMap()));
            return new ArraySchema().items(schema);
          }
        }
        if (ClassUtil.isBasicType(aClass)) {
          throw new RuntimeException("不支持的类型");
        }
        if (BeanUtil.isBean(aClass)) {
          Type[] types = parameterizedType.getActualTypeArguments();
          if (ArrayUtil.isNotEmpty(types)&&!(types[0] instanceof WildcardType)) {
            metadata = new SchemaMetadata(metadata.type(), getParameterizedTypeName(metadata.type()), metadata.description(), metadata.schemaMap());
          }
          return getBeanSchema(metadata, aClass);
        }
      }

    }

    if (metadata.type() instanceof GenericArrayType genericArrayType) {
      Type genericComponentType = genericArrayType.getGenericComponentType();
      Schema<?> schema = handle(new SchemaMetadata(genericComponentType, "", "", metadata.schemaMap()));
      return new ArraySchema().items(schema);
    }


    if (metadata.type() instanceof Class<?> aClass) {
      if (ClassUtil.isBasicType(aClass)) {
        return new StringSchema().name(metadata.name()).description(metadata.description());
      }
      if (aClass.isArray()) {
        Class<?> componentType = aClass.getComponentType();
        Schema<?> schema = handle(new SchemaMetadata(componentType, "", "", metadata.schemaMap()));
        return new ArraySchema().items(schema);
      }
      if (BeanUtil.isBean(aClass)) {
        return getBeanSchema(metadata, aClass);
      }
    }
    return new Schema<>();
  }

  private Schema getBeanSchema(SchemaMetadata metadata, Class<?> aClass) {
    Map<String, Schema> p = new HashMap<>();
    Arrays.stream(ClassUtil.getDeclaredFields(aClass))
      .filter(field -> !Modifier.isStatic(field.getModifiers()))
      .filter(field -> !Modifier.isTransient(field.getModifiers()))
      .filter(field -> !Modifier.isFinal(field.getModifiers()))
      .filter(field -> !AnnotatedElementUtils.hasAnnotation(field, JsonIgnore.class))
      .forEach(field -> {
        String desc = Optional.ofNullable(field.getAnnotation(io.swagger.v3.oas.annotations.media.Schema.class))
          .map(io.swagger.v3.oas.annotations.media.Schema::description)
          .orElse("");
        Type type = field.getGenericType();
        if (type instanceof TypeVariable<?> typeVariable) {
          if (metadata.type() instanceof ParameterizedType parameterizedType) {
            type = parameterizedType.getActualTypeArguments()[0];
          }
        }

        SchemaMetadata schemaMetadata = new SchemaMetadata(type, field.getName(), desc, metadata.schemaMap());
        Schema<?> schema = handle(schemaMetadata);
        //是object 入map
        p.put(field.getName(), schema);
      });
    Schema schema = new ObjectSchema().properties(p);
    String name = StrUtil.blankToDefault(metadata.name(), aClass.getSimpleName());
    metadata.schemaMap().put(name, schema);
    String $ref = COMPONENTS_SCHEMAS + name;
    return new ObjectSchema().$ref($ref);
  }

  private String getParameterizedTypeName(Type type) {
    if (type instanceof ParameterizedType parameterizedType) {
      Type rawType = parameterizedType.getRawType();
      if (rawType instanceof Class<?> aClass) {
        Type[] typeArguments = parameterizedType.getActualTypeArguments();
        if (typeArguments.length == 1) {
          Type typeArgument = typeArguments[0];
          if (typeArgument instanceof ParameterizedType) {
            return aClass.getSimpleName() + "<" + getParameterizedTypeName(typeArgument) + ">";
          }
          if (typeArgument instanceof Class<?> aClass1) {
            return aClass.getSimpleName() + "<" + aClass1.getSimpleName() + ">";
          }
        }
      }
    }
    return type.getTypeName();
  }

  public static void main(String[] args) throws NoSuchMethodException, IOException {
    HashMap<String, Schema<?>> schemaMap = new HashMap<>();
    Type test = DefaultSchemaTypeHandler.class.getMethod("test").getGenericReturnType();
    SchemaMetadata schemaMetadata = new SchemaMetadata(test, "list", "", schemaMap);
    Schema<?> schema = new DefaultSchemaTypeHandler().handle(schemaMetadata);
    System.out.println(schemaMap);
    System.out.println(schema);

  }

  public Result<Map<String,SchemaMetadata>> test() {
    return Result.success();
  }

}

