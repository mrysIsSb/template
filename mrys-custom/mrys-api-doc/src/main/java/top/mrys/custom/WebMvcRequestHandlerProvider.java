package top.mrys.custom;

/**
 * @author mrys
 */


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.media.FileSchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.HeaderParameter;
import io.swagger.v3.oas.models.parameters.PathParameter;
import io.swagger.v3.oas.models.parameters.QueryParameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import jakarta.servlet.ServletContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping;
import top.mrys.custom.customizers.ApiPathsCustomizer;
import top.mrys.custom.customizers.ApiSchemaCustomizer;
import top.mrys.custom.schema.DefaultSchemaTypeHandler;
import top.mrys.custom.schema.SchemaMetadata;
import top.mrys.custom.schema.SchemaTypeHandler;

import java.lang.reflect.Parameter;
import java.util.*;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@Slf4j
public class WebMvcRequestHandlerProvider implements ApiPathsCustomizer, ApiSchemaCustomizer {
  private final List<RequestMappingInfoHandlerMapping> handlerMappings;
  private final String contextPath;

  private Paths paths;

  private Map<String, Schema<?>> schemas;

  private SchemaTypeHandler schemaTypeHandler = new DefaultSchemaTypeHandler();


  @Autowired
  public WebMvcRequestHandlerProvider(
    Optional<ServletContext> servletContext,
    List<RequestMappingInfoHandlerMapping> handlerMappings) {
    this.handlerMappings = handlerMappings;
    this.contextPath = servletContext
      .map(ServletContext::getContextPath).orElse("");
    log.info("contextPath:{}", contextPath);
    init();
  }

  public void init() {
    this.paths = new Paths();
    this.schemas = new HashMap<>();


    handlerMappings.forEach(handlerMapping -> {
      handlerMapping.getHandlerMethods().forEach((requestMappingInfo, handlerMethod) -> {
        if (!handlerMethod.hasMethodAnnotation(io.swagger.v3.oas.annotations.Operation.class)) {
          return;
        }
        String path = requestMappingInfo.getPatternValues().stream().findFirst().orElse("");
        if (StrUtil.isBlank(path)) {
          return;
        }
        PathItem item = new PathItem();
        Operation operation = new Operation();
        Optional.ofNullable(AnnotatedElementUtils.findMergedAnnotation(handlerMethod.getBeanType(), Tag.class))
          .ifPresent(tag -> operation.addTagsItem(tag.name()));
        Arrays.stream(handlerMethod.getMethod().getAnnotationsByType(io.swagger.v3.oas.annotations.Operation.class))
          .findFirst().ifPresent(op -> {
            operation.summary(op.summary());
            operation.description(op.description());
          });
        requestMappingInfo.getMethodsCondition().getMethods().forEach(method -> {
          switch (method) {
            case GET -> item.get(operation);
            case POST -> item.post(operation);
            case PUT -> item.put(operation);
            case DELETE -> item.delete(operation);
            case PATCH -> item.patch(operation);
            case HEAD -> item.head(operation);
            case OPTIONS -> item.options(operation);
            case TRACE -> item.trace(operation);
          }
        });
        //请求参数
        Arrays.stream(handlerMethod.getMethod().getParameters()).forEach(param -> {
          //请求体
          if (AnnotatedElementUtils.hasAnnotation(param, RequestBody.class)) {
            String desc = Optional.ofNullable(param.getAnnotation(io.swagger.v3.oas.annotations.media.Schema.class))
              .map(io.swagger.v3.oas.annotations.media.Schema::description)
              .or(() -> Optional.ofNullable(param.getAnnotation(io.swagger.v3.oas.annotations.Parameter.class))
                .map(io.swagger.v3.oas.annotations.Parameter::description))
              .orElse("");
            operation.requestBody(new io.swagger.v3.oas.models.parameters.RequestBody()
              .content(new io.swagger.v3.oas.models.media.Content()
                .addMediaType("application/json", new io.swagger.v3.oas.models.media.MediaType()
                  .schema(schemaTypeHandler.handle(new SchemaMetadata(ObjectUtil.defaultIfNull(param.getParameterizedType(), param.getType()), param.getName(), desc, this.schemas))))));

          }
          //请求地址参数
          else if (AnnotatedElementUtils.hasAnnotation(param, PathVariable.class)) {
            PathVariable pathVariable = param.getAnnotation(PathVariable.class);
            PathParameter pathParameter = new PathParameter();
            pathParameter.setName(StrUtil.blankToDefault(pathVariable.name(), param.getName()));

            putParamDescription(param, pathParameter);
            operation.addParametersItem(pathParameter);
          }
          //请求头
          else if (AnnotatedElementUtils.hasAnnotation(param, RequestHeader.class)) {
            RequestHeader requestHeader = param.getAnnotation(RequestHeader.class);
            HeaderParameter headerParameter = new HeaderParameter();
            headerParameter.required(requestHeader.required());
            headerParameter.setName(StrUtil.blankToDefault(requestHeader.name(), param.getName()));
            putParamDescription(param, headerParameter);
            operation.addParametersItem(headerParameter);
          }
          //请求参数
          else if (AnnotatedElementUtils.hasAnnotation(param, RequestParam.class)) {
            RequestParam requestParam = param.getAnnotation(RequestParam.class);
            QueryParameter parameter = new QueryParameter();
            parameter.required(requestParam.required());
            parameter.setName(StrUtil.blankToDefault(requestParam.name(), param.getName()));
            putParamDescription(param, parameter);
            operation.addParametersItem(parameter);
          }
          //文件
          else if (AnnotatedElementUtils.hasAnnotation(param, RequestPart.class) && param.getType().equals(MultipartFile.class)) {
            RequestPart requestPart = param.getAnnotation(RequestPart.class);
            io.swagger.v3.oas.models.parameters.RequestBody body = new io.swagger.v3.oas.models.parameters.RequestBody();
            Map<String, Schema> p = new HashMap<>() {{
              put(StrUtil.blankToDefault(requestPart.name(), param.getName()), new FileSchema());
            }};
            body.content(new io.swagger.v3.oas.models.media.Content()
              .addMediaType("multipart/form-data", new io.swagger.v3.oas.models.media.MediaType()
                .schema(new ObjectSchema().properties(p))));
            operation.requestBody(body);
          } else {
            if (ClassUtil.isBasicType(param.getType())) {
              QueryParameter parameter = new QueryParameter();
              parameter.required(true);
              parameter.setName(param.getName());
              putParamDescription(param, parameter);
              operation.addParametersItem(parameter);
            } else if (param.getType().isArray()
              || Collection.class.isAssignableFrom(param.getType())
              || BeanUtil.isBean(param.getType())) {
              QueryParameter parameter = new QueryParameter();
              parameter.required(true);
              parameter.setName(param.getName());
              parameter.schema(schemaTypeHandler.handle(new SchemaMetadata(ObjectUtil.defaultIfNull(param.getParameterizedType(), param.getType()), param.getName(), "", this.schemas)));
              putParamDescription(param, parameter);
              operation.addParametersItem(parameter);
            } else {
              QueryParameter parameter = new QueryParameter();
              parameter.required(true);
              parameter.setName(param.getName());
              putParamDescription(param, parameter);
              operation.addParametersItem(parameter);
            }
          }
        });

        //响应
        operation.responses(new ApiResponses()
          .addApiResponse("200", new ApiResponse()
            .description("成功")
            .content(new io.swagger.v3.oas.models.media.Content()
              .addMediaType("application/json", new io.swagger.v3.oas.models.media.MediaType()
                .schema(schemaTypeHandler.handle(new SchemaMetadata(ObjectUtil.defaultIfNull(handlerMethod.getMethod().getGenericReturnType(), handlerMethod.getMethod().getReturnType()), handlerMethod.getMethod().getName(), "", this.schemas)))))));
        this.paths.addPathItem(path, item);
      });
    });
  }

  @Override
  public void customize(Paths paths) {
    paths.putAll(this.paths);
  }

  private static void putParamDescription(Parameter param, io.swagger.v3.oas.models.parameters.Parameter parameter) {
    Optional.ofNullable(param.getAnnotation(io.swagger.v3.oas.annotations.Parameter.class))
      .map(io.swagger.v3.oas.annotations.Parameter::description)
      .ifPresent(parameter::setDescription);
  }

  @Override
  public void customize(Map<String, Schema> schemas) {
    schemas.putAll(this.schemas);
  }
}