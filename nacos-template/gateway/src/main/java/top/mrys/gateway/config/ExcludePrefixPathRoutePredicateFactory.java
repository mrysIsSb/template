package top.mrys.gateway.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import org.springframework.cloud.gateway.handler.predicate.AbstractRoutePredicateFactory;
import org.springframework.cloud.gateway.handler.predicate.GatewayPredicate;
import org.springframework.web.server.ServerWebExchange;
import top.mrys.gateway.config.ExcludePrefixPathRoutePredicateFactory.Config;

/**
 * @author mrys
 * @date 2022/9/11
 */
public class ExcludePrefixPathRoutePredicateFactory extends AbstractRoutePredicateFactory<Config> {


  public ExcludePrefixPathRoutePredicateFactory() {
    super(Config.class);
  }

  @Override
  public Predicate<ServerWebExchange> apply(Config config) {
    return (GatewayPredicate) exchange -> {

      String path = exchange.getRequest().getURI().getRawPath();

      String[] split = path.split("/");

      Optional<String> first = Arrays.stream(split).filter(Objects::nonNull)
        .map(String::trim)
        .filter(s -> !s.isEmpty())
        .findFirst();
      if (first.isPresent()) {
        String s = first.get();
        List<String> excludePrefix = config.getExcludePrefix();
        if (excludePrefix.contains(s)) {
          return false;
        }
      }
      return true;
    };
  }

  @Override
  public List<String> shortcutFieldOrder() {
    return Collections.singletonList("excludePrefix");
  }

  @Override
  public ShortcutType shortcutType() {
    return ShortcutType.GATHER_LIST;
  }

  public static class Config {

    private List<String> excludePrefix;

    public List<String> getExcludePrefix() {
      return excludePrefix;
    }

    public void setExcludePrefix(List<String> excludePrefix) {
      this.excludePrefix = excludePrefix;
    }
  }
}
