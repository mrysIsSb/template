package top.mrys.custom.filters.rules;

import cn.hutool.core.collection.CollUtil;
import top.mrys.custom.core.FilterChain;
import top.mrys.custom.core.ServerExchange;
import top.mrys.custom.exceptions.RuleNoPassException;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
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
    if (rules.stream()
      .peek(rule -> exchange.getAttrs().remove(Rule.RULE_EXCEPTION_MSG))
      .allMatch(rule -> rule.test(exchange))) {
      chain.doFilter(exchange);
    } else {
      Optional.ofNullable(exchange.getAttrs().get(Rule.RULE_EXCEPTION_MSG))
        .ifPresent(msg -> {
          throw new RuleNoPassException((String) msg);
        });
      throw new RuleNoPassException("not pass rules");
    }
  }
}
