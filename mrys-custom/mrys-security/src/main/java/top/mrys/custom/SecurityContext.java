package top.mrys.custom;

public interface SecurityContext {
  void clearContext();

  Authentication getAuthentication();

  void setAuthentication(Authentication authentication);
}
