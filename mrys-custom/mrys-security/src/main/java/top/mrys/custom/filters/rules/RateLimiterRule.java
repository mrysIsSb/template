package top.mrys.custom.filters.rules;

import com.google.common.util.concurrent.RateLimiter;
import top.mrys.custom.core.ServerExchange;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 限流规则
 * @author mrys
 */
public class RateLimiterRule extends AbstractRuleWrapper<RateLimiterRule> implements Rule {

  private String key;

  private int limit;

  private static ConcurrentHashMap<String,RateLimiter> rateLimiterMap = new ConcurrentHashMap<>();

  public RateLimiterRule(String key, int limit) {
    this.key = key;
    this.limit = limit;
  }

  @Override
  public boolean test(ServerExchange exchange) {
    RateLimiter rateLimiter = rateLimiterMap.computeIfAbsent(key, k -> RateLimiter.create(limit));
    if(rateLimiter.tryAcquire()){
      return super.test(exchange);
    }
    exchange.getAttrs().put(Rule.RULE_EXCEPTION_MSG,"访问过于频繁");
    return false;
  }
}
