package top.mrys.custom.core;

import org.springframework.http.HttpStatus;

public interface Response<C> {

  C getNativeResponse();

  Response<C> ret(HttpStatus status, String data);

}
