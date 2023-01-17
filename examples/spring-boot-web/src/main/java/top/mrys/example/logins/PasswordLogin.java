package top.mrys.example.logins;

import cn.hutool.crypto.digest.MD5;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.ServerRequest;
import top.mrys.core.Result;
import top.mrys.core.ResultException;
import top.mrys.custom.core.Authentication;
import top.mrys.custom.core.LoginFunction;
import top.mrys.custom.core.Request;
import top.mrys.custom.core.UserInfo;
import top.mrys.custom.filters.AccessTokenAuthentication;
import top.mrys.custom.login.functions.LoginFunctionResult;
import top.mrys.example.domain.SysLoginPassword;
import top.mrys.example.service.LoginService;

/**
 * @author mrys
 */
@Component
public class PasswordLogin implements LoginFunction, LoginFunctionResult<AccessTokenAuthentication> {


  @Autowired
  private MemoryTokenAuthenticateProvider memoryTokenAuthenticateProvider;

  @Autowired
  private LoginService loginService;

  @Override
  public boolean support(String type) {
    return "pwd".equals(type);
  }

  @Override
  public Authentication login(Request<ServerRequest> request) {
    PasswordLoginParam body = request.getBody(PasswordLoginParam.class);
    SysLoginPassword one = new SysLoginPassword()
      .selectOne(new LambdaQueryWrapper<SysLoginPassword>()
        .eq(SysLoginPassword::getUsername, body.getUsername()));
    if (one == null) {
      throw new ResultException("用户名或密码错误");
    }

    if (!one.getPassword().equals(MD5.create().digestHex(body.getPassword()))) {
      throw new ResultException("用户名或密码错误");
    }

    UserInfo info = loginService.selectUserInfoByUid(one.getUid().toString());

    String token = memoryTokenAuthenticateProvider.addUser(info);
    AccessTokenAuthentication authentication = new AccessTokenAuthentication(token);
    authentication.setUserInfo(info);
    authentication.setAuthenticated(true);
    return authentication;
  }

  @Override
  public Result<?> getResult(AccessTokenAuthentication authentication) {
    return Result.success(authentication.getAccessToken());
  }

  @Setter
  @Getter
  public static class PasswordLoginParam {
    private String username;
    private String password;
  }
}
