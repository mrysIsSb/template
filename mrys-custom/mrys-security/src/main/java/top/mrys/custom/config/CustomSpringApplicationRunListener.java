package top.mrys.custom.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import top.mrys.custom.core.SecurityContext;

import java.time.Duration;

/**
 * @author mrys
 * @date 2022/12/16 14:45
 */
@Slf4j
public class CustomSpringApplicationRunListener implements SpringApplicationRunListener {

  @Override
  public void started(ConfigurableApplicationContext context, Duration timeTaken) {
    log.info("spring boot started set security context");
    try {
      SecurityContext bean = context.getBean(SecurityContext.class);
    } catch (Exception e) {
      log.error("spring boot started set security context error", e);
    }
  }

}
