package top.mrys.core;

/**
 * @author mrys
 * @date 2022/9/16
 */
public class PageParam {

  private Long current = 1L;

  private Long size = 10L;

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

  public static PageParam from(Long current, Long size) {
    PageParam pageParam = new PageParam();
    pageParam.setCurrent(current);
    pageParam.setSize(size);
    return pageParam;
  }
  //toString
  public String toString() {
    return "PageParam{current=%d, size=%d}".formatted(current, size);
  }

}
