package top.mrys.custom;

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

    List<String> getRoles();

    List<String> getPermissions();
}
