package top.mrys.custom;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import top.mrys.core.PageParam;
import top.mrys.core.PageParamConvertor;

/**
 * @author mrys
 */
public class PageDTOConvertor implements PageParamConvertor<Page> {

  @Override
  public boolean support(Class<?> clazz) {
    return IPage.class.isAssignableFrom(clazz);
  }

  @Override
  public Page convert(PageParam pageParam) {
    return PageTool.toPage(pageParam);
  }

}
