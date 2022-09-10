package top.mrys.gateway.config;

import java.net.URI;
import java.util.Collections;
import java.util.Optional;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.config.conditional.ConditionalOnEnabledFilter;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.InMemoryRouteDefinitionRepository;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import reactor.core.publisher.Mono;

/**
 * @author mrys
 */
@Configuration(proxyBeanMethods = false)
public class GatewayAutoConfiguration {

  @Autowired
  private Optional<InMemoryRouteDefinitionRepository> inMemoryRouteDefinitionRepository;

  @Bean
  @ConditionalOnEnabledFilter
  public ReplaceHostFromPathGatewayFilterFactory replaceHostFromPathGatewayFilterFactory() {
    return new ReplaceHostFromPathGatewayFilterFactory();
  }

  @PostConstruct
  public void addRouteDefinitionLocator() {
    inMemoryRouteDefinitionRepository.ifPresent(repository -> {
      RouteDefinition routeDefinition = new RouteDefinition();
      routeDefinition.setId("general-route");
      routeDefinition.setUri(URI.create("lb://host"));
      routeDefinition.setPredicates(Collections.singletonList(new PredicateDefinition("Path=/**")));
      routeDefinition.setFilters(
        Collections.singletonList(new FilterDefinition("ReplaceHostFromPath")));
      routeDefinition.setOrder(Ordered.LOWEST_PRECEDENCE - 100);
      repository.save(Mono.just(routeDefinition)).subscribe();
    });
  }

}
