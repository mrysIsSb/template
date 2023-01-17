package top.mrys.example.service;

import top.mrys.custom.core.UserInfo;

/**
 * @author mrys
 */
public interface LoginService {

  UserInfo selectUserInfoByUid(String uid);
}
