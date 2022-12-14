package top.mrys.custom;

import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.stream.Collectors;

/**
 * spring bean 实例提供者
 * 从spring容器中获取bean
 */
public class SpringInstanceProvider implements InstanceProvider {

  private final ApplicationContext applicationContext;

  public SpringInstanceProvider(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  @Override
  public <T> T getInstance(Class<T> clazz) {
    return applicationContext.getBean(clazz);
  }

  @Override
  public <T> List<T> getInstances(Class<T> clazz) {
    return applicationContext.getBeansOfType(clazz).values().stream().collect(Collectors.toList());
  }
}
