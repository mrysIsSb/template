package top.mrys.custom.core;

public interface SecurityContext {
  void clearContext();

  Authentication getAuthentication();

  void setAuthentication(Authentication authentication);
}
