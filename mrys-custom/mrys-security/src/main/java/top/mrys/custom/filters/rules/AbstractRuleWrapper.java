package top.mrys.custom.filters.rules;

import top.mrys.custom.core.ServerExchange;

/**
 * @author mrys
 */
public abstract class AbstractRuleWrapper<R extends Rule> implements Rule {

  protected Rule rule;

  public R rule(Rule rule) {
    this.rule = rule;
    return (R) this;
  }

  @Override
  public boolean test(ServerExchange exchange) {
    if (rule != null) {
      exchange.getAttrs().remove(Rule.RULE_EXCEPTION_MSG);
      return rule.test(exchange);
    }
    return true;
  }
}
