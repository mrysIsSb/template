package top.mrys.custom.login.functions;

import top.mrys.core.Result;
import top.mrys.custom.core.Authentication;

/**
 * 登录成功返回json
 * @author mrys
 */
public interface LoginFunctionResult<T extends Authentication> {
    Result<?> getResult(T authentication);
}
