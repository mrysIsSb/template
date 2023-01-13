package top.mrys.custom.filters.rules;

import top.mrys.custom.core.Authentication;
import top.mrys.custom.core.ServerExchange;
import top.mrys.custom.core.UserInfo;

import java.util.function.Predicate;

/**
 * @author mrys
 */
public class UserRule extends AbstractRuleWrapper<UserRule> implements Rule {
  private Predicate<UserInfo> predicate;

  public UserRule(Predicate<UserInfo> predicate) {
    this.predicate = predicate;
  }

  @Override
  public boolean test(ServerExchange exchange) {
    Authentication authentication = exchange.getSecurityContext().getAuthentication();
    if (authentication == null) {
      return true;
    }
    if (authentication.isAuthenticated() && predicate.test(authentication.getUserInfo())) {
      return super.test(exchange);
    }
    return true;
  }
}
