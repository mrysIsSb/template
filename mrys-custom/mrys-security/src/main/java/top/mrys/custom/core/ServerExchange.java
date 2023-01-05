package top.mrys.custom.core;


import java.util.Map;

/**
 * @see org.springframework.web.server.ServerWebExchange
 */
public interface ServerExchange {

  Request getRequest();

  Response getResponse();

  Map<String, Object> getAttrs();

  SecurityContext getSecurityContext();

  InstanceProvider instanceProvider();

}
