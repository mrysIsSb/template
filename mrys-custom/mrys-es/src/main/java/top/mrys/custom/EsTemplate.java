package top.mrys.custom;

import cn.hutool.json.JSONObject;

import java.io.Serializable;

public interface EsTemplate {
  /**
   * 保存
   */
  <T> T save(T t);

  /**
   * 删除
   */
  <T> void deleteById(Serializable t, Class<T> clazz);

  <T> void deleteAll(Class<T> clazz);

  /**
   * 更新
   */
  <T> T updateById(T t);

  /**
   * 查询
   */
  <T> T findById(Serializable t, Class<T> clazz);


  /**
   * 查询
   */
  Object search(SearchOption searchOption, JSONObject body, Class<?> clazz);

}
