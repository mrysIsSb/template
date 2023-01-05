package top.mrys.custom.core;

import org.springframework.http.HttpMethod;

public interface Request<C> {

  HttpMethod getMethod();

  String getPath();

  String getHeader(String name);

  String getParam(String name);

  default <T> T getBody(Class<T> clazz){
    throw new UnsupportedOperationException();
  }

  C getNativeRequest();
}
