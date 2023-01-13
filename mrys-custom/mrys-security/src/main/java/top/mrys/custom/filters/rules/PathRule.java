package top.mrys.custom.filters.rules;

import org.springframework.util.AntPathMatcher;
import top.mrys.custom.core.ServerExchange;

/**
 * 匹配路径规则
 *
 * @author mrys
 */
public class PathRule extends AbstractRuleWrapper<PathRule> implements Rule {


  private AntPathMatcher matcher = new AntPathMatcher();

  private String path;

  public PathRule(String path) {
    this.path = path;
  }

  @Override
  public boolean test(ServerExchange exchange) {
    if (matcher.match(path, exchange.getRequest().getPath())) {
      return super.test(exchange);
    }
    return true;
  }
}
