package top.mrys.gateway.config;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.gateway.config.conditional.ConditionalOnEnabledFilter;
import org.springframework.cloud.gateway.config.conditional.ConditionalOnEnabledPredicate;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PathRoutePredicateFactory;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.InMemoryRouteDefinitionRepository;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import reactor.core.publisher.Mono;
import top.mrys.gateway.config.swagger.GatewaySwaggerGatewayFilterFactory;

/**
 * @author mrys
 */
@Configuration(proxyBeanMethods = false)
public class GatewayAutoConfiguration {

  @Autowired
  private DiscoveryClient discoveryClient;
  @Autowired
  private Optional<InMemoryRouteDefinitionRepository> inMemoryRouteDefinitionRepository;

  @Bean
  @ConditionalOnEnabledFilter
  public ReplaceHostFromPathGatewayFilterFactory replaceHostFromPathGatewayFilterFactory() {
    return new ReplaceHostFromPathGatewayFilterFactory();
  }

  @Bean
  @ConditionalOnEnabledFilter
  @Order(-1)
  public GatewaySwaggerGatewayFilterFactory gatewaySwaggerGatewayFilterFactory() {
    return new GatewaySwaggerGatewayFilterFactory();
  }

  @Bean
  @ConditionalOnEnabledPredicate
  public ExcludePrefixPathRoutePredicateFactory excludePrefixPathRoutePredicateFactory() {
    return new ExcludePrefixPathRoutePredicateFactory();
  }

  @PostConstruct
  public void addRouteDefinitionLocator() {
    inMemoryRouteDefinitionRepository.ifPresent(repository -> {
      RouteDefinition routeDefinition = new RouteDefinition();
      routeDefinition.setId("general-route");
      routeDefinition.setUri(URI.create("lb://host"));
      routeDefinition.setPredicates(Collections.singletonList(
        new PredicateDefinition("ExcludePrefixPath=actuator,doc.html,swagger-ui.html,,swagger-ui,swagger-resources,webjars,favicon.ico")));
      routeDefinition.setFilters(
        Arrays.asList(new FilterDefinition("ReplaceHostFromPath"),
          new FilterDefinition("GatewaySwagger=http://localhost:10002")/*,
          new FilterDefinition("ModifyResponseBody")*/));
      routeDefinition.setOrder(Ordered.LOWEST_PRECEDENCE - 100);
      repository.save(Mono.just(routeDefinition)).subscribe();
    });
  }

}
