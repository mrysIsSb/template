package top.mrys.custom.filters.rules;

import org.springframework.util.AntPathMatcher;
import top.mrys.custom.core.ServerExchange;

/**
 * @author mrys
 */
public class PathRule implements Rule {


  private AntPathMatcher matcher = new AntPathMatcher();

  private String path;
  private Rule rule;

  public PathRule(String path) {
    this.path = path;
  }

  public PathRule rule(Rule rule) {
    this.rule = rule;
    return this;
  }

  @Override
  public boolean test(ServerExchange exchange) {
    if (matcher.match(path, exchange.getRequest().getPath())) {
      if (rule != null) {
        return rule.test(exchange);
      }
      return true;
    }
    return true;
  }
}
