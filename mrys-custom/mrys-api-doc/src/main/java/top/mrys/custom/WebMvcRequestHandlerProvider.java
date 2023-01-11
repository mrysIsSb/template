package top.mrys.custom;

/**
 * @author mrys
 */


import jakarta.servlet.ServletContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping;

import java.util.List;
import java.util.Optional;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@Slf4j
public class WebMvcRequestHandlerProvider {
  private final List<RequestMappingInfoHandlerMapping> handlerMappings;
  private final String contextPath;

  @Autowired
  public WebMvcRequestHandlerProvider(
    Optional<ServletContext> servletContext,
    List<RequestMappingInfoHandlerMapping> handlerMappings) {
    this.handlerMappings = handlerMappings;
    this.contextPath = servletContext
      .map(ServletContext::getContextPath).orElse("");
    log.info("contextPath:{}", contextPath);
  }

}