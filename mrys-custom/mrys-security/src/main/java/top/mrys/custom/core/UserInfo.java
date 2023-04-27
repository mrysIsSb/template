package top.mrys.custom.core;

import java.util.List;
import java.util.Optional;

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
   * 租户id
   * @return
   */
    String getTenantId();

    /**
     * 是否是超级管理员
     */
    boolean isSuperAdmin();

    List<String> getRoles();

    List<String> getPermissions();

    <T> Optional<T> getAttr(String name);


}
