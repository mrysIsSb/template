package top.mrys.custom.core;

import java.util.List;

/**
 * 当前用户信息
 *
 * @author mrys
 * @date 2022/12/16 14:48
 */
public interface UserInfo {
    /**
     * 用户id
     */
    String getUserId();

    String getUserName();

    /**
     * 是否是超级管理员
     */
    boolean isSuperAdmin();

    List<String> getRoles();

    List<String> getPermissions();
}
