package top.mrys.custom.core;


/**
 * @see org.springframework.security.core.context.ThreadLocalSecurityContextHolderStrategy
 */
public class ThreadLocalSecurityContext implements SecurityContext {

  private static final ThreadLocal<Authentication> authenticationThreadLocal = new ThreadLocal<>();

  @Override
  public void clearContext() {
    authenticationThreadLocal.remove();
  }

  public void setAuthentication(Authentication authentication) {
    authenticationThreadLocal.set(authentication);
  }

  public Authentication getAuthentication() {
    return authenticationThreadLocal.get();
  }
}
