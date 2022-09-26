package top.mrys.custom;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchOption {
  private Integer from = 0;
  private Integer size = 10;

  private String sort;

  private Boolean explain = false;
//  private Boolean lenient = false;

  private Boolean pretty = false;

}
