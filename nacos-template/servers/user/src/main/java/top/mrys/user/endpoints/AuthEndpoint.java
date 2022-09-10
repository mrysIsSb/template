package top.mrys.user.endpoints;

import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

/**
 * @author mrys
 */
@Component
@Endpoint(id = "auth")
public class AuthEndpoint {

  @ReadOperation
  public Map<String, Object> auth() {
    return new HashMap<String, Object>() {{
      put("auth", "auth");
    }};
  }
}
