package top.mrys.custom.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import top.mrys.custom.core.SecurityContext;
import top.mrys.custom.core.SecurityContextHolder;

import java.time.Duration;

/**
 * @author mrys
 * @date 2022/12/16 14:45
 */
@Slf4j
public class CustomSpringApplicationRunListener implements SpringApplicationRunListener {

  @Override
  public void started(ConfigurableApplicationContext context, Duration timeTaken) {
    log.debug("spring boot started");
    SecurityContextHolder.setContext(context.getBean(SecurityContext.class));
  }

}
