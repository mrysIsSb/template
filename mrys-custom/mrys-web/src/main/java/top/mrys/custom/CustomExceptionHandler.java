package top.mrys.custom;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.Nullable;

/**
 * @author mrys
 */
public interface CustomExceptionHandler {

  boolean handler(HttpServletRequest request, HttpServletResponse response, @Nullable Object handler, Exception ex);
}
