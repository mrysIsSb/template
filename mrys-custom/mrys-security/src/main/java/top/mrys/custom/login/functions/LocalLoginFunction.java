package top.mrys.custom.login.functions;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.springframework.web.servlet.function.ServerRequest;
import top.mrys.core.Result;
import top.mrys.custom.config.SecurityProperties;
import top.mrys.custom.core.Authentication;
import top.mrys.custom.core.LoginFunction;
import top.mrys.custom.core.Request;
import top.mrys.custom.filters.AccessTokenAuthentication;

import java.util.List;

/**
 * @author mrys
 */
public class LocalLoginFunction implements LoginFunction, LoginFunctionResult<AccessTokenAuthentication> {

    private final List<SecurityProperties.User> users;

    public LocalLoginFunction(List<SecurityProperties.User> users) {
        this.users = users;
    }

    @Override
    public boolean support(String type) {
        return "local".equals(type);
    }

    @Override
    public Authentication login(Request<ServerRequest> request) {
        JSONObject body = JSONUtil.parseObj(request.getBody(String.class));
        String username = body.getStr("username");
        String password = body.getStr("password");
        if (username == null || password == null) {
            return null;
        }
        return users.stream()
                .filter(user -> user.getUserName().equals(username) && user.getPassword().equals(password))
                .findFirst()
                .map(user -> {
                    AccessTokenAuthentication authentication = new AccessTokenAuthentication(user.getToken());
                    authentication.setAuthenticated(true);
                    authentication.setUserInfo(user.toUserInfo());
                    return authentication;
                }).orElse(null);
    }

    @Override
    public Result<?> getResult(AccessTokenAuthentication authentication) {
        return Result.success(authentication.getAccessToken());
    }
}
