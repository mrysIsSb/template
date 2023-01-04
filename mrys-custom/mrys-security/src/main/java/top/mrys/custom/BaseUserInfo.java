package top.mrys.custom;

import lombok.Data;

import java.util.List;

/**
 * @author mrys
 * @date 2022/12/16 15:00
 */
@Data
public class BaseUserInfo implements UserInfo {

  private String userId;
  private String userName;

  private boolean superAdmin = false;

  private List<String> roles;

  private List<String> permissions;

}
