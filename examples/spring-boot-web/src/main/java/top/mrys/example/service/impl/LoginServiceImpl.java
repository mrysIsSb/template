package top.mrys.example.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.mrys.custom.core.BaseUserInfo;
import top.mrys.custom.core.UserInfo;
import top.mrys.example.domain.SysUser;
import top.mrys.example.mapper.SysUserMapper;
import top.mrys.example.service.LoginService;

import java.util.HashMap;

/**
 * @author mrys
 */
@Service
public class LoginServiceImpl implements LoginService {

  @Autowired
  private SysUserMapper sysUserMapper;

  @Override
  public UserInfo selectUserInfoByUid(String uid) {
    SysUser sysUser = sysUserMapper.selectById(uid);
    BaseUserInfo info = new BaseUserInfo();
    info.setUserId(sysUser.getUid().toString());
    info.setUserName(sysUser.getUsername());
    info.setSuperAdmin(false);
    info.setAttrs(new HashMap<>() {{
      put("nickname", sysUser.getNickname());
      put("avatar", sysUser.getAvatar());
    }});
    return info;
  }
}
