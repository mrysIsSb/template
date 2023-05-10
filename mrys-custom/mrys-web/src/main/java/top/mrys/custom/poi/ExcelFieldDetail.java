package top.mrys.custom.poi;

import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Field;
import java.math.BigDecimal;

/**
 * 2022-07-06
 * by: mrys
 */
@Setter
@Getter
public class ExcelFieldDetail {

  private String fieldName = "";

  private String fieldType = "";

  private String targetAttr;
  //提示
  private String prompt = "";

  //设置只能选择不能输入的列内容.
  private String[] combo = {};

  private Field field;
  private String defaultValue = "";
  private String suffix = "";

  private Integer sort = Integer.MAX_VALUE;

  //行高
  private double height = 14;
  private double width = 16;

  private Integer scale = 2;
  private Integer roundingMode = BigDecimal.ROUND_HALF_EVEN;

  private String dateFormat = "";
  private String readConverterExp;

  private String separator = ",";

  //  private Class<ExcelHandlerAdapter> handler;
  private Class<? extends ExcelHandlerAdapter> handler;
  private String[] args = {};


  private boolean statistics = false;

  private Excel.ColumnType cellType = Excel.ColumnType.STRING;

  public ExcelFieldDetail setExcel(Excel excel) {
    this.fieldName = excel.name();
    this.targetAttr = excel.targetAttr();
    this.prompt = excel.prompt();
    this.combo = excel.combo();
    this.sort = excel.sort();
    this.height = excel.height();
    this.width = excel.width();
    this.scale = excel.scale();
    this.roundingMode = excel.roundingMode();
    this.dateFormat = excel.dateFormat();
    this.readConverterExp = excel.readConverterExp();
    this.separator = excel.separator();
    this.handler = excel.handler();
    this.args = excel.args();
    this.statistics = excel.isStatistics();
    this.cellType = excel.cellType();
    return this;
  }

}
