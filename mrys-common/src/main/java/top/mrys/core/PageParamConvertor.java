package top.mrys.core;

/**
 * @author mrys
 */
public interface PageParamConvertor<T> {

  boolean support(Class<?> clazz);

  T convert(PageParam pageParam);

}
