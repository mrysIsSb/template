package top.mrys.custom.schema;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.models.media.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.reflect.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

/**
 * @author mrys
 */
@Slf4j
public class DefaultSchemaTypeHandler implements SchemaTypeHandler {

  private static final Map<Type, SchemaTypeHandler> schemaTypeHandlerMap = new HashMap<>();
  public static final String COMPONENTS_SCHEMAS = "#/components/schemas/";

  static {
    schemaTypeHandlerMap.put(Map.class, metadata -> new ObjectSchema().name(metadata.name()).description(metadata.description()));
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
    //是否存在schemaTypeHandler
    SchemaTypeHandler handler = schemaTypeHandlerMap.get(metadata.type());
    //存在直接调用返回
    if (handler != null) {
      return handler.handle(metadata);
    }
    //type是泛型 如：List<String>
    if (metadata.type() instanceof ParameterizedType parameterizedType) {
      Type rawType = parameterizedType.getRawType();
      if (rawType instanceof Class<?> aClass) {
        //如果是集合
        if (Collection.class.isAssignableFrom(aClass)) {
          Type[] typeArguments = parameterizedType.getActualTypeArguments();
          if (typeArguments.length == 1) {
            Type typeArgument = typeArguments[0];
            Schema<?> schema = handle(new SchemaMetadata(typeArgument, "", "", metadata.schemaMap()));
            return new ArraySchema().items(schema);
          }
        }
        //如果是基本类型
        if (ClassUtil.isBasicType(aClass)) {
          throw new RuntimeException("不支持的类型");
        }
        //如果是bean
        if (BeanUtil.isBean(aClass)) {
          metadata = new SchemaMetadata(metadata.type(), getParameterizedTypeName(metadata.type()), metadata.description(), metadata.schemaMap());
          return getBeanSchema(metadata, aClass);
        }
      }

    }
    //type是数组 如：String[]
    if (metadata.type() instanceof GenericArrayType genericArrayType) {
      //获取数组的类型
      Type genericComponentType = genericArrayType.getGenericComponentType();
      Schema<?> schema = handle(new SchemaMetadata(genericComponentType, "", "", metadata.schemaMap()));
      return new ArraySchema().items(schema);
    }

    if (metadata.type() instanceof Class<?> aClass) {
      //如果是基本类型
      if (ClassUtil.isBasicType(aClass)) {
        return new StringSchema().name(metadata.name()).description(metadata.description());
      }
      //如果是数组
      if (aClass.isArray()) {
        Class<?> componentType = aClass.getComponentType();
        Schema<?> schema = handle(new SchemaMetadata(componentType, "", "", metadata.schemaMap()));
        return new ArraySchema().items(schema);
      }
      //如果是bean
      if (BeanUtil.isBean(aClass)) {
        metadata = new SchemaMetadata(metadata.type(), getParameterizedTypeName(metadata.type()), metadata.description(), metadata.schemaMap());
        return getBeanSchema(metadata, aClass);
      }
      //如果是enum
      if (aClass.isEnum()) {
        return new StringSchema().name(metadata.name())
          .examples(Arrays.asList(aClass.getEnumConstants()))
          .description(metadata.description());
      }
    }
    return new Schema<>();
  }

  //用来存储已经处理过的类，防止循环引用
  private ThreadLocal<Set<Class>> threadLocal = new ThreadLocal<>();

  private Schema getBeanSchema(SchemaMetadata metadata, Class<?> aClass) {
    log.debug("解析:{}", aClass);
    Map<String, Schema> p = new HashMap<>();
    Set<Class> set = threadLocal.get();
    if (set == null) {
      set = new HashSet<>();
    } else if (set.contains(aClass)) {
      return new Schema();
    }
    set.add(aClass);
    threadLocal.set(set);

    getFields(aClass).stream()
      .filter(field -> !Modifier.isStatic(field.getModifiers()))
      .filter(field -> !Modifier.isTransient(field.getModifiers()))
      .filter(field -> !Modifier.isFinal(field.getModifiers()))
      .filter(field -> !AnnotatedElementUtils.hasAnnotation(field, JsonIgnore.class))
      .filter(field -> {
        io.swagger.v3.oas.annotations.media.Schema schema = field.getAnnotation(io.swagger.v3.oas.annotations.media.Schema.class);
        return !(schema != null && schema.hidden());
      })
      .filter(field -> {
        Set<Class> set1 = threadLocal.get();
        return !set1.contains(field.getType());
      })
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
    //解析getset方法
    Arrays.stream(ClassUtil.getPublicMethods(aClass))
      .filter(method -> {
        return method.getName().startsWith("get") || method.getName().startsWith("is");
      }).forEach(method -> {
        String name = method.getName();
        if (name.startsWith("get")) {
          name = name.substring(3);
        } else if (name.startsWith("is")) {
          name = name.substring(2);
        }
        name = StrUtil.lowerFirst(name);
        if (p.containsKey(name)) {
          return;
        }
        //不存在
        String desc = Optional.ofNullable(AnnotatedElementUtils.findMergedAnnotation(method, io.swagger.v3.oas.annotations.media.Schema.class))
          .map(io.swagger.v3.oas.annotations.media.Schema::description)
          .orElse("");
        Type type = method.getReturnType();
        //如果是Class
        if (type.equals(Class.class)) {
          return;
        }

        if (type instanceof TypeVariable<?> typeVariable) {
          if (metadata.type() instanceof ParameterizedType parameterizedType) {
            type = parameterizedType.getActualTypeArguments()[0];
          }
        }

        SchemaMetadata schemaMetadata = new SchemaMetadata(type, name, desc, metadata.schemaMap());
        Schema<?> schema = handle(schemaMetadata);
        //是object 入map
        p.put(name, schema);
      });


    Schema schema = new ObjectSchema().properties(p);
    String name = StrUtil.blankToDefault(metadata.name(), aClass.getSimpleName());
    metadata.schemaMap().put(name, schema);
    String $ref = COMPONENTS_SCHEMAS + name;
    set = threadLocal.get();
    set.remove(aClass);
    threadLocal.set(set);
    return new ObjectSchema().$ref($ref);
  }

  public List<Field> getFields(Class<?> aClass) {
    List<Field> fields = new ArrayList<>();
    Class<?> currentClass = aClass;
    while (currentClass != null && currentClass != Object.class) {
      fields.addAll(Arrays.asList(currentClass.getDeclaredFields()));
      currentClass = currentClass.getSuperclass();
    }
    return fields;
  }

  private String getParameterizedTypeName(Type type) {
    return type.getTypeName();
    //todo 内部类 没解决
//    if (type instanceof ParameterizedType parameterizedType) {
//      Type rawType = parameterizedType.getRawType();
//      if (rawType instanceof Class<?> aClass) {
//        Type[] typeArguments = parameterizedType.getActualTypeArguments();
//        if (typeArguments.length == 1) {
//          Type typeArgument = typeArguments[0];
//          if (typeArgument instanceof ParameterizedType) {
//            return aClass.getSimpleName() + "<" + getParameterizedTypeName(typeArgument) + ">";
//          }
//          if (typeArgument instanceof Class<?> aClass1) {
//            return aClass.getSimpleName() + "<" + aClass1.getSimpleName() + ">";
//          }
//        }
//      }
//    }
//    return type.getTypeName();
  }

}


