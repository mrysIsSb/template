package top.mrys.custom.mvc;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpMethod;
import top.mrys.custom.Request;

public class MvcRequest implements Request {

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

  public HttpServletRequest getHttpServletRequest() {
    return request;
  }
}
