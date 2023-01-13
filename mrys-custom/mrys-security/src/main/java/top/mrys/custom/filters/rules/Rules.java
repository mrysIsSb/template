package top.mrys.custom.filters.rules;

import cn.hutool.core.collection.CollUtil;
import top.mrys.custom.core.FilterChain;
import top.mrys.custom.core.ServerExchange;

import java.util.HashSet;
import java.util.Set;

/**
 * @author mrys
 */
public class Rules {

  private final Set<Rule> rules = new HashSet<>();

  public Rules add(Rule rule) {
    rules.add(rule);
    return this;
  }

  public void doFilter(ServerExchange exchange, FilterChain chain) {
    if (CollUtil.isEmpty(rules)) {
      chain.doFilter(exchange);
      return;
    }
    //通过所有规则
    if (rules.stream().allMatch(rule -> rule.test(exchange))) {
      chain.doFilter(exchange);
    }else {
      throw new RuntimeException("not pass rules");
    }
  }
}
