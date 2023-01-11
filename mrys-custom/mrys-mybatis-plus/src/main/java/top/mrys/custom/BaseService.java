package top.mrys.custom;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import top.mrys.core.PageParam;
import top.mrys.core.PageResult;
import top.mrys.core.Result;

import java.io.Serializable;

/**
 * 基础服务
 *
 * @author mrys
 */
public interface BaseService<T> {

  PageResult<T> pageResult(PageParam pageParam, T t);

  T getById(Serializable id);

  Boolean insert(T t);

  Boolean updateById(T t);

  /**
   * 删除
   *
   * @author mrys
   */
  Integer delByIds(Serializable[] ids);

  default <D> PageResult<D> toPageResult(Page<D> page) {
    PageResult<D> result = PageResult.from(
      Result.success(page.getRecords()));
    result.setCurrent(page.getCurrent());
    result.setSize(page.getSize());
    result.setTotal(page.getTotal());
    return result;
  }
}
