package top.mrys.custom.filters;

import org.springframework.core.Ordered;
import top.mrys.custom.core.FilterChain;
import top.mrys.custom.core.SecurityFilter;
import top.mrys.custom.core.ServerExchange;
import top.mrys.custom.filters.customizers.RuleCustomizer;
import top.mrys.custom.filters.rules.Rules;

/**
 * @author mrys
 */
public class RuleFilter implements SecurityFilter, Ordered {
  @Override
  public int getOrder() {
    return OrderConstants.ORDER_RULE;
  }

  @Override
  public void doFilter(ServerExchange exchange, FilterChain chain) {
    Rules rules = new Rules();
    exchange.instanceProvider()
      .getInstances(RuleCustomizer.class)
      .forEach(customizer -> customizer.customize(rules));
    rules.doFilter(exchange, chain);
  }
}
