package top.mrys.custom.mvc;

import lombok.SneakyThrows;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.function.ServerRequest;
import top.mrys.custom.core.Request;

/**
 * @author mrys
 */
public class HandlerFunctionRequest implements Request<ServerRequest> {

    private final ServerRequest request;

    public HandlerFunctionRequest(ServerRequest request) {
        this.request = request;
    }

    @Override
    public HttpMethod getMethod() {
        return request.method();
    }

    @Override
    public String getPath() {
        return request.path();
    }

    @Override
    public String getHeader(String name) {
        return request.headers().header(name).get(0);
    }

    @Override
    public String getParam(String name) {
        return request.param(name).orElse(null);
    }

    @Override
    @SneakyThrows
    public <T> T getBody(Class<T> clazz) {
        return request.body(clazz);
    }

    @Override
    public ServerRequest getNativeRequest() {
        return request;
    }
}
