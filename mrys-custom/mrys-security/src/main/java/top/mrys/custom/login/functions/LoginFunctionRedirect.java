package top.mrys.custom.login.functions;

import top.mrys.custom.core.Authentication;

/**
 * @author mrys
 */
public interface LoginFunctionRedirect<T extends Authentication> {

    String getRedirectUrl(T authentication);
}
