package top.mrys.custom;

import org.springframework.http.HttpMethod;

public interface Request {

  HttpMethod getMethod();

  String getPath();

  String getHeader(String name);
}
