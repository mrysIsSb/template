package top.mrys.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author mrys
 */
@SpringBootApplication
@EnableDiscoveryClient
public class GatewayBoot {

  public static void main(String[] args) {
    SpringApplication.run(GatewayBoot.class, args);
  }

}
