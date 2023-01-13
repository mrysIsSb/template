package top.mrys.custom.filters.rules;

import top.mrys.custom.core.ServerExchange;

/**
 * 用户角色规则
 * 1.用户必须登录
 * 2.用户必须有角色
 *
 * @author mrys
 */
public class UserRoleRule extends AbstractRuleWrapper<UserRoleRule> implements Rule {

  private String role;

  public UserRoleRule(String role) {
    this.role = role;
  }

  @Override
  public UserRoleRule rule(Rule rule) {
    return super.rule(new UserRule(userInfo -> userInfo.getRoles().contains(role)).rule(rule));
  }

  @Override
  public boolean test(ServerExchange exchange) {
    return super.test(exchange);
  }
}
