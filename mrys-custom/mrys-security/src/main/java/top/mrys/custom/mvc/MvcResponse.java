package top.mrys.custom.mvc;

import top.mrys.custom.Response;

import javax.servlet.http.HttpServletResponse;

public class MvcResponse implements Response {

  private final javax.servlet.http.HttpServletResponse response;

  public MvcResponse(HttpServletResponse response) {
    this.response = response;
  }
}
