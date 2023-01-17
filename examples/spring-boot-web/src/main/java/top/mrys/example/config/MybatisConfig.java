package top.mrys.example.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author mrys
 */
@Configuration(proxyBeanMethods = false)
@MapperScan("top.mrys.example.mapper")
public class MybatisConfig {

  
}
