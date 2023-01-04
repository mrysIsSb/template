package top.mrys.custom.mvc;

import top.mrys.custom.Response;

import jakarta.servlet.http.HttpServletResponse;

public class MvcResponse implements Response {

  private final HttpServletResponse response;

  public MvcResponse(HttpServletResponse response) {
    this.response = response;
  }

  public HttpServletResponse getHttpServletResponse() {
    return response;
  }
}
