package top.mrys.custom.customizers;

import io.swagger.v3.oas.models.servers.Server;

import java.util.List;

/**
 * @author mrys
 */
public interface ApiServerCustomizer {

  void customize(List<Server> servers);
}
