package top.mrys.gateway.config;

import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.addOriginalRequestUrl;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * @author mrys
 * @date 2022/9/11
 */
public class ReplaceHostFromPathGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {

  @Override
  public GatewayFilter apply(Object config) {
    return (exchange, chain) -> {
      ServerHttpRequest request = exchange.getRequest();
      addOriginalRequestUrl(exchange, request.getURI());
      String path = request.getURI().getRawPath();
      String[] originalParts = StringUtils.tokenizeToStringArray(path, "/");

      StringBuilder newPath = new StringBuilder();
      for (int i = 1; i < originalParts.length; i++) {
        newPath.append('/');
        newPath.append(originalParts[i]);
      }
      if (newPath.length() > 1 && path.endsWith("/")) {
        newPath.append('/');
      }

      ServerHttpRequest newRequest = request.mutate()
        .uri(UriComponentsBuilder.newInstance()
          .scheme(request.getURI().getScheme())
          .host(originalParts[0])
          .query(request.getURI().getQuery())
          .build().toUri())
        .path(newPath.toString())
        .build();

      Route route = exchange.getRequiredAttribute(GATEWAY_ROUTE_ATTR);
      Route newRoute = Route.async()
        .asyncPredicate(route.getPredicate())
        .filters(route.getFilters())
        .id(route.getId())
        .order(route.getOrder())
        .uri(UriComponentsBuilder.fromUri(route.getUri())
          .host(originalParts[0])
          .build().toUri())
        .build();
      exchange.getAttributes().put(GATEWAY_ROUTE_ATTR, newRoute);

      exchange.getAttributes().put(GATEWAY_REQUEST_URL_ATTR,
        newRequest.getURI());

      return chain.filter(exchange.mutate().request(newRequest).build());
    };
  }

}
