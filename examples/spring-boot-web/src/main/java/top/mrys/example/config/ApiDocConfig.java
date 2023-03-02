package top.mrys.example.config;

import cn.hutool.json.JSONUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;
import top.mrys.custom.EnableApiDoc;
import top.mrys.custom.OpenApiProvider;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author mrys
 */
@EnableApiDoc
@Configuration(proxyBeanMethods = false)
public class ApiDocConfig {

  //  @Bean
//  public ApiInfoCustomizer apiInfoCustomizer() {
//    return info -> info.title("mrys-web").version("1.0");
//  }
  @Bean
  public RouterFunction<ServerResponse> routerFunction(OpenApiProvider openApiProvider) {
    return RouterFunctions
      .route()
      .GET("/v3/api-doc", request -> ServerResponse.ok().body(JSONUtil.toJsonStr(openApiProvider.getOpenApi())))
      .build();
  }

//  public static void main(String[] args) {
////      line(BigDecimal.valueOf(150),BigDecimal.valueOf(240),600,6);
//      line(BigDecimal.valueOf(150),BigDecimal.valueOf(240),2000,20);
////      line(BigDecimal.valueOf(30),BigDecimal.valueOf(40),3000,10);
//  }

  public static void line(BigDecimal begin, BigDecimal end, Integer num, Integer rangeNum){
      BigDecimal divide = end.divide(begin, 2,  RoundingMode.HALF_UP);
      double pow = Math.pow(divide.doubleValue(), 1.0 / rangeNum)-1;
      System.out.println(pow);
      int i = num / rangeNum;
      do {
          System.out.println(begin+"--"+num+"股--"+begin.multiply(BigDecimal.valueOf(num))+"元");
          begin = begin.multiply(BigDecimal.valueOf(pow + 1)).setScale(2, RoundingMode.HALF_UP);
          num-=i;
      } while (num>=0);
  }
}