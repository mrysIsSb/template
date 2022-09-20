package top.mrys.custom;

import cn.hutool.core.util.ArrayUtil;
import java.lang.annotation.Annotation;
import java.util.Map;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;

/**
 * @author mrys
 */
public class SpringTool implements ApplicationContextAware, BeanFactoryPostProcessor {

  private static ApplicationContext applicationContext;
  private static ConfigurableListableBeanFactory beanFactory;

  @Override
  public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
    SpringTool.applicationContext = applicationContext;
  }

  @Override
  public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory)
    throws BeansException {
    SpringTool.beanFactory = beanFactory;
  }

  public static ListableBeanFactory getBeanFactory() {
    return null == beanFactory ? applicationContext : beanFactory;
  }

  public static <T> T getBean(@NonNull Class<T> clazz) {
    return getBeanFactory().getBean(clazz);
  }

  public static <T> T getBean(@NonNull String name, @NonNull Class<T> clazz) {
    return getBeanFactory().getBean(name, clazz);
  }

  public static <T> T getBean(@NonNull String name) {
    return (T) getBeanFactory().getBean(name);
  }

  public static <T> Map<String, T> getBeansOfType(@NonNull Class<T> clazz) {
    return getBeanFactory().getBeansOfType(clazz);
  }

  public static Map<String,Object> getBeansOfAnnotation(@NonNull Class<? extends Annotation> clazz){
    return getBeanFactory().getBeansWithAnnotation(clazz);
  }

  public static String getProperty(String key) {
    if (null == applicationContext) {
      return null;
    }
    return applicationContext.getEnvironment().getProperty(key);
  }

  public static String getProperty(String key, String defaultValue) {
    if (null == applicationContext) {
      return defaultValue;
    }
    return applicationContext.getEnvironment().getProperty(key, defaultValue);
  }

  public static <T> T getProperty(String key, Class<T> clazz) {
    if (null == applicationContext) {
      return null;
    }
    return applicationContext.getEnvironment().getProperty(key, clazz);
  }

  public static String[] getActiveProfiles() {
    if (null == applicationContext) {
      return null;
    }
    return applicationContext.getEnvironment().getActiveProfiles();
  }

  public static String getActiveProfile() {
    final String[] activeProfiles = getActiveProfiles();
    return ArrayUtil.isNotEmpty(activeProfiles) ? activeProfiles[0] : null;
  }

  public static String[] getDefaultProfiles() {
    if (null == applicationContext) {
      return null;
    }
    return applicationContext.getEnvironment().getDefaultProfiles();
  }

  public static boolean isDev(){
    return ArrayUtil.contains(getActiveProfiles(),"dev");
  }

  public static boolean isTest(){
    return ArrayUtil.contains(getActiveProfiles(),"test");
  }

  public static boolean isProd(){
    return ArrayUtil.contains(getActiveProfiles(),"prod");
  }

}
