package top.mrys.custom.mvc;

import top.mrys.custom.core.InstanceProvider;
import top.mrys.custom.core.SecurityContext;
import top.mrys.custom.core.ServerExchange;

import java.util.HashMap;
import java.util.Map;

public class MvcServerExchange implements ServerExchange {

  private final MvcRequest request;

  private final MvcResponse response;

  private final Map<String, Object> attrs = new HashMap<>();

  private final SecurityContext securityContext;

  private final InstanceProvider instanceProvider;

  public MvcServerExchange(MvcRequest request, MvcResponse response, SecurityContext securityContext, InstanceProvider instanceProvider) {
    this.request = request;
    this.response = response;
    this.securityContext = securityContext;
    this.instanceProvider = instanceProvider;
  }

  @Override
  public MvcRequest getRequest() {
    return request;
  }

  @Override
  public MvcResponse getResponse() {
    return response;
  }

  @Override
  public Map<String, Object> getAttrs() {
    return attrs;
  }

  @Override
  public SecurityContext getSecurityContext() {
    return securityContext;
  }

  @Override
  public InstanceProvider instanceProvider() {
    return instanceProvider;
  }
}
