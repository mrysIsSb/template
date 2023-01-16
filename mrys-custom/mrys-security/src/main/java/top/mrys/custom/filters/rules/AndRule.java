package top.mrys.custom.filters.rules;

import top.mrys.custom.core.ServerExchange;

/**
 * @author mrys
 */
public class AndRule implements Rule {

  private Rule[] rules;

  public AndRule(Rule... rules) {
    this.rules = rules;
  }

  @Override
  public boolean test(ServerExchange exchange) {
    for (Rule rule : rules) {
      if (!rule.test(exchange)) {
        return false;
      }
    }
    return true;
  }
}
