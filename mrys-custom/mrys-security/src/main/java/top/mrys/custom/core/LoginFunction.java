package top.mrys.custom.core;

import org.springframework.web.servlet.function.ServerRequest;

/**
 * 登录功能
 * @author mrys
 */
public interface LoginFunction {

    boolean support(String type);

    Authentication login(Request<ServerRequest> request);
}
