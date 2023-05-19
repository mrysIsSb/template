package top.mrys.custom.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import top.mrys.custom.core.SecurityContext;
import top.mrys.custom.core.SecurityContextHolder;

import java.time.Duration;

/**
 * @author mrys
 * @date 2022/12/16 14:45
 */
@Slf4j
public class CustomSpringApplicationRunListener implements SpringApplicationRunListener {

//  @Override
//  public void started(ConfigurableApplicationContext context, Duration timeTaken) {
//    setSecurityCOntext(context);
//  }

  @Override
  public void ready(ConfigurableApplicationContext context, Duration timeTaken) {
    setSecurityContext(context);
  }

  private static void setSecurityContext(ConfigurableApplicationContext context) {
    log.info("spring boot started set security context:{}", context.getClass());
    try {
      if (context instanceof ConfigurableWebApplicationContext) {
        SecurityContext bean = context.getBean(SecurityContext.class);
        SecurityContextHolder.setContext(bean);
      }
    } catch (Exception e) {
      log.error("spring boot started set security context error", e);
    }
  }

}
