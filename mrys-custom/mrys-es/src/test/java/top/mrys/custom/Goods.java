package top.mrys.custom;

import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@ToString
@Index("goods")
public class Goods {

  @Id
  private String id;

  private String name;

  private String desc;

  private BigDecimal price;

  private String img;

  private String type;

  private String brand;

  private Date createTime;
}
