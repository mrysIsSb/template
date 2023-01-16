package top.mrys.custom.mvc;

import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import top.mrys.custom.core.Response;

import jakarta.servlet.http.HttpServletResponse;

public class MvcResponse implements Response<HttpServletResponse> {

  private final HttpServletResponse response;

  public MvcResponse(HttpServletResponse response) {
    this.response = response;
  }

  public HttpServletResponse getHttpServletResponse() {
    return response;
  }

  @Override
  public HttpServletResponse getNativeResponse() {
    return response;
  }

  @SneakyThrows
  @Override
  public Response<HttpServletResponse> ret(HttpStatus status, String data) {
    response.setCharacterEncoding("UTF-8");
    response.setContentType("application/json");
    response.setStatus(status.value());
    response.getWriter().write(data);
    return this;
  }
}
