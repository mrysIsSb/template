package top.mrys.custom;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.io.Serializable;
import top.mrys.core.PageParam;
import top.mrys.core.PageResult;
import top.mrys.core.Result;

/**
 * @author mrys
 */
public interface BaseService<T> {
  PageResult<T> pageResult(PageParam pageParam, T t);

  T getById(Serializable id);

  Boolean insert(T t);

  Boolean updateById(T t);

  Integer delByIds(Serializable[] ids);

  default <D> PageResult<D> toPageResult(Page<D> page) {
    PageResult<D> result = PageResult.from(
        Result.ok(page.getRecords()));
    result.setCurrent(page.getCurrent());
    result.setTotal(page.getTotal());
    return result;
  }
}
