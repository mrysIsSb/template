package top.mrys.custom.filters.rules;

import top.mrys.custom.core.ServerExchange;

import java.util.Arrays;
import java.util.function.Predicate;

/**
 * @author mrys
 */
public interface Rule extends Predicate<ServerExchange> {

  //存放规则异常信息
  String RULE_EXCEPTION_MSG = "rule_exception_msg";

  static Rule and(Rule... rules) {
    return serverExchange -> Arrays.stream(rules)
      .allMatch(rule -> rule.test(serverExchange));
  }

  static Rule or(Rule... rules) {
    return serverExchange -> Arrays.stream(rules)
      .anyMatch(rule -> rule.test(serverExchange));
  }
}
