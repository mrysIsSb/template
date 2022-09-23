package top.mrys.core;

import cn.hutool.core.bean.BeanUtil;

/**
 * 转换器
 * @Author mrys
 */
public interface PojoConvert<T> {

  default T from(Object o) {
    BeanUtil.copyProperties(o, this);
    return (T) this;
  }

  default <E> E to(Class<E> clazz) {
    return BeanUtil.toBean(this, clazz);
  }
}
