package top.mrys.core;


import java.util.List;

/**
 * @author mrys
 * @date 2022/9/16
 */
public class PageResult<T> extends Result<List<T>> {

  private Long current = 1L;
  private Long size = 10L;
  private Long total = 0L;

  public Long getCurrent() {
    return current;
  }

  public void setCurrent(Long current) {
    this.current = current;
  }

  public Long getSize() {
    return size;
  }

  public void setSize(Long size) {
    this.size = size;
  }

  public Long getTotal() {
    return total;
  }

  public void setTotal(Long total) {
    this.total = total;
  }

  public static <T> PageResult<T> from(Result<List<T>> result) {
    return from(result, PageParam.from(1L, 10L));
  }

  public static <T> PageResult<T> from(Result<List<T>> result, PageParam pageParam) {
    return from(result, pageParam, 0L);
  }

  public static <T> PageResult<T> from(Result<List<T>> result, PageParam pageParam, Long total) {
    PageResult<T> pageResult = new PageResult<>();
    pageResult.setCode(result.getCode());
    pageResult.setMsg(result.getMsg());
    pageResult.setData(result.getData());
    pageResult.setCurrent(pageParam.getCurrent());
    pageResult.setSize(pageParam.getSize());
    pageResult.setTotal(total);
    return pageResult;
  }
}
