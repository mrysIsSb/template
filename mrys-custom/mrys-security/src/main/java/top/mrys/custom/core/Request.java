package top.mrys.custom.core;

import org.springframework.http.HttpMethod;

public interface Request {

  HttpMethod getMethod();

  String getPath();

  String getHeader(String name);
}
