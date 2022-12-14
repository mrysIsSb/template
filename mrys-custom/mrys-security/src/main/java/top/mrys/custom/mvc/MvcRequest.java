package top.mrys.custom.mvc;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpMethod;
import top.mrys.custom.core.Request;

public class MvcRequest implements Request<HttpServletRequest> {

  private final HttpServletRequest request;

  public MvcRequest(HttpServletRequest request) {
    this.request = request;
  }

  @Override
  public HttpMethod getMethod() {
    return HttpMethod.valueOf(request.getMethod());
  }

  @Override
  public String getPath() {
    return request.getRequestURI();
  }

  @Override
  public String getHeader(String name) {
    return request.getHeader(name);
  }

  @Override
  public String getParam(String name) {
    return request.getParameter(name);
  }


  @Override
  public HttpServletRequest getNativeRequest() {
    return request;
  }

}
