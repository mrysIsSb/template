package top.mrys.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import top.mrys.api.user.UserApi;

/**
 * @author mrys
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(clients = {
  UserApi.class
})
public class AuthBoot {

  public static void main(String[] args) {
    SpringApplication.run(AuthBoot.class, args);
  }

}
