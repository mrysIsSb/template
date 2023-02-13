package top.mrys.custom.filters.rules;

import top.mrys.custom.core.ServerExchange;

/**
 * @author mrys
 */
public class PermitAll implements Rule{
  @Override
  public boolean test(ServerExchange serverExchange) {
    return true;
  }
}
