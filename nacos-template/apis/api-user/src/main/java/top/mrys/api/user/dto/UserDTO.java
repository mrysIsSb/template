package top.mrys.api.user.dto;

/**
 * @author mrys
 * @date 2022/9/6
 */
public class UserDTO {
  private String Username;
  private String password;

  public String getUsername() {
    return Username;
  }

  public void setUsername(String username) {
    Username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
