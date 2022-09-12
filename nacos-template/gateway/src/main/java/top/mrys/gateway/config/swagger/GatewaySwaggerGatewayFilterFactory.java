package top.mrys.gateway.config.swagger;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import top.mrys.gateway.config.ReplaceHostFromPathGatewayFilterFactory;
import top.mrys.gateway.config.swagger.GatewaySwaggerGatewayFilterFactory.Config;

/**
 * @author mrys
 * @date 2022/9/12
 */
public class GatewaySwaggerGatewayFilterFactory extends AbstractGatewayFilterFactory<Config> {

  @Override
  public Class<Config> getConfigClass() {
    return Config.class;
  }

  @Override
  public GatewayFilter apply(Config config) {
    return new OrderedGatewayFilter((exchange, chain) -> {
      String rawPath = exchange.getRequest().getURI().getRawPath();
      if (!rawPath.endsWith("/api-docs")) {
        //直接返回
        return chain.filter(exchange);
      }
      ServerHttpResponse originalResponse = exchange.getResponse();
      DataBufferFactory bufferFactory = originalResponse.bufferFactory();
      ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(
        originalResponse) {
        @Override
        public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
          if (body instanceof Flux) {
            config.setPath(exchange.getAttribute(ReplaceHostFromPathGatewayFilterFactory.SERVER_ID));
            Flux<? extends DataBuffer> fluxBody = (Flux<? extends DataBuffer>) body;
            return super.writeWith(fluxBody.buffer().map(dataBuffers -> {
              DataBufferFactory dataBufferFactory = new DefaultDataBufferFactory();
              DataBuffer join = dataBufferFactory.join(dataBuffers);
              byte[] content = new byte[join.readableByteCount()];
              join.read(content);
              // 释放掉内存
              DataBufferUtils.release(join);
              String s = new String(content, StandardCharsets.UTF_8);
              // 这里可以对返回的数据进行处理
              s = modifyBody(s, config);
              byte[] bytes = s.getBytes(StandardCharsets.UTF_8);
              getDelegate().getHeaders().setContentLength(bytes.length);
              return bufferFactory.wrap(bytes);
            }));
          }
          return super.writeWith(body);
        }
      };
      return chain.filter(exchange.mutate().response(decoratedResponse).build());
    },-100);
  }

  private String modifyBody(String jsonStr, Config config) {
    if (StringUtils.isEmpty(jsonStr.trim())) {
      return jsonStr;
    }
    JSONObject jsonObject = JSONUtil.parseObj(jsonStr);
    jsonObject.set("host", config.getHost());
    JSONObject paths = jsonObject.getJSONObject("paths");
    JSONObject newPaths = new JSONObject();
    if (paths != null && paths.size() > 0) {
      Set<String> keys = paths.keySet();
      keys.forEach(key -> newPaths.set(config.getPath() + key, paths.getObj(key)));
    }
    jsonObject.set("paths", newPaths);
    JSONObject setting = new JSONObject();
    setting.set("language", "zh-CN");
    setting.set("enableSwaggerModels", true);
    setting.set("swaggerModelName", "Swagger Models");
    setting.set("enableReloadCacheParameter", true);
    setting.set("enableAfterScript", true);
    setting.set("enableDocumentManage", true);
    setting.set("enableVersion", true);
    setting.set("enableRequestCache", true);
    setting.set("enableFilterMultipartApis", true);
    setting.set("enableFilterMultipartApiMethodType", "POST");
    setting.set("enableHost", true);
    setting.set("enableHostText", "enableHostText");
    setting.set("enableDynamicParameter", true);
    setting.set("enableDebug", false);
    setting.set("enableFooter", true);
    setting.set("enableFooterCustom", true);
    setting.set("footerCustomContent", "footerCustomContent");
    setting.set("enableSearch", false);
    setting.set("enableHomeCustom", true);
    setting.set("homeCustomLocation", "homeCustomLocation");
    setting.set("enableGroup", true);

    JSONObject openapi = new JSONObject();
    openapi.set("x-setting", setting);
    jsonObject.set("x-openapi", openapi);
    return jsonObject.toString();
  }

  @Override
  public List<String> shortcutFieldOrder() {
    return Collections.singletonList("host");
  }

  public static class Config {

    private String host;

    private String path;

    public String getHost() {
      return host;
    }

    public void setHost(String host) {
      this.host = host;
    }

    public String getPath() {
      return path;
    }

    public void setPath(String path) {
      this.path = path;
    }
  }

}
