package top.mrys.custom.mvc;

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
}
