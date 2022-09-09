package top.mrys.admin;

import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author mrys
 */
@SpringBootApplication
@EnableAdminServer
@EnableDiscoveryClient
public class AdminBoot {

  public static void main(String[] args) {
    SpringApplication.run(AdminBoot.class, args);
    LoggerFactory.getILoggerFactory().getLogger("root").info("admin 启动成功");
  }
}
