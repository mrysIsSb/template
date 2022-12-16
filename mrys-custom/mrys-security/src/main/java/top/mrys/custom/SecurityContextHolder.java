package top.mrys.custom;

/**
 * @author mrys
 * @date 2022/12/16 14:42
 */
public class SecurityContextHolder {
  public static SecurityContext context;

  public static void setContext(SecurityContext context) {
    SecurityContextHolder.context = context;
  }

  public static SecurityContext getContext() {
    return context;
  }

}
