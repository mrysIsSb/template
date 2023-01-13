package top.mrys.custom.filters.rules;

import top.mrys.custom.core.ServerExchange;

import java.util.function.Predicate;

/**
 * @author mrys
 */
public interface Rule extends Predicate<ServerExchange> {
}
