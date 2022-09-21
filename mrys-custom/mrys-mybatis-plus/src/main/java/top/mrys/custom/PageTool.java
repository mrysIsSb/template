package top.mrys.custom;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import top.mrys.core.PageParam;

/**
 * @author mrys
 */
public class PageTool {

  public static <T> Page<T> toPage(PageParam pageParam) {
    return new PageDTO<>(pageParam.getCurrent(), pageParam.getSize());
  }

}
