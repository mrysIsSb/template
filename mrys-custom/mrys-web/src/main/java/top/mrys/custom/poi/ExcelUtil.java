package top.mrys.custom.poi;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.mrys.core.ResultException;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Excel相关处理
 * 参考 ruoyi
 *
 * @author mrys
 */
public class ExcelUtil<T> {
  private static final Logger log = LoggerFactory.getLogger(ExcelUtil.class);

  /**
   * Excel sheet最大行数，默认65536
   */
  public static final int sheetSize = 65536;

  /**
   * 工作表名称
   */
  private String sheetName;

  /**
   * 导出类型（EXPORT:导出数据；IMPORT：导入模板）
   */
  private Excel.Type type;

  /**
   * 工作薄对象
   */
  public Workbook wb;

  /**
   * 工作表对象
   */
  private Sheet sheet;

  /**
   * 样式列表
   */
  private Map<String, CellStyle> styles;

  /**
   * 导入导出数据列表
   */
  private List<T> list;

  /**
   * 注解列表
   */
  private List<ExcelFieldDetail> fields;

  /**
   * 当前行号
   */
  private int rownum;

  /**
   * 标题
   */
  private String title;

  /**
   * 最大高度
   */
  private short maxHeight;

  /**
   * 统计列表
   */
  private Map<Integer, Double> statistics = new HashMap<Integer, Double>();

  /**
   * 数字格式
   */
  private static final DecimalFormat DOUBLE_FORMAT = new DecimalFormat("######0.00");

  /**
   * 实体对象
   */
  public Class<T> clazz;
  public String dateFormat;

  public List<Function<Field, ExcelFieldDetail>> fieldFunctions = new ArrayList<>();

  public ExcelUtil(Class<T> clazz) {
    this.clazz = clazz;
  }

  public void init(List<T> list, String sheetName, String title, Excel.Type type) {
    if (list == null) {
      list = new ArrayList<T>();
    }
    this.list = list;
    this.sheetName = sheetName;
    this.type = type;
    this.title = title;
    createExcelField();
    createWorkbook();
    createTitle();
  }

  /**
   * 创建excel第一行标题
   */
  public void createTitle() {
    if (StrUtil.isNotBlank(title)) {
      Row titleRow = sheet.createRow(rownum == 0 ? rownum++ : 0);
      titleRow.setHeightInPoints(30);
      Cell titleCell = titleRow.createCell(0);
      titleCell.setCellStyle(styles.get("title"));
      titleCell.setCellValue(title);
      sheet.addMergedRegion(new CellRangeAddress(titleRow.getRowNum(), titleRow.getRowNum(), 0,
        this.fields.size() - 1));
    }
  }

  /**
   * 对excel表单默认第一个索引名转换成list
   *
   * @param is 输入流
   * @return 转换后集合
   */
  public List<T> importExcel(InputStream is) throws Exception {
    return importExcel(is, 0);
  }

  /**
   * 对excel表单默认第一个索引名转换成list
   *
   * @param is       输入流
   * @param titleNum 标题占用行数
   * @return 转换后集合
   */
  public List<T> importExcel(InputStream is, int titleNum) throws Exception {
    return importExcel("", is, titleNum);
  }

  /**
   * 对excel表单指定表格索引名转换成list
   *
   * @param sheetName 表格索引名
   * @param titleNum  标题占用行数
   * @param is        输入流
   * @return 转换后集合
   */
  public List<T> importExcel(String sheetName, InputStream is, int titleNum) throws Exception {
    this.type = Excel.Type.IMPORT;
    this.wb = WorkbookFactory.create(is);
    List<T> list = new ArrayList<T>();
    // 如果指定sheet名,则取指定sheet中的内容 否则默认指向第1个sheet
    Sheet sheet = StrUtil.isNotBlank(sheetName) ? wb.getSheet(sheetName) : wb.getSheetAt(0);
    if (sheet == null) {
      throw new IOException("文件sheet不存在");
    }

    // 获取最后一个非空行的行下标，比如总行数为n，则返回的为n-1
    int rows = sheet.getLastRowNum();

    if (rows > 0) {
      // 定义一个map用于存放excel列的序号和field.
      Map<String, Integer> cellMap = new HashMap<>();
      // 获取表头
      Row heard = sheet.getRow(titleNum);
      for (int i = 0; i < heard.getPhysicalNumberOfCells(); i++) {
        Cell cell = heard.getCell(i);
        if (ObjectUtil.isNotNull(cell)) {
          String value = this.getCellValue(heard, i).toString();
          cellMap.put(value, i);
        } else {
          cellMap.put(null, i);
        }
      }
      // 有数据时才处理 得到类的所有field.
      List<ExcelFieldDetail> fields = this.getFields();
      Map<Integer, ExcelFieldDetail> fieldsMap = new HashMap<>();
      for (ExcelFieldDetail detail : fields) {
        Integer column = cellMap.get(detail.getFieldName());
        if (column != null) {
          fieldsMap.put(column, detail);
        }
      }
      for (int i = titleNum + 1; i <= rows; i++) {
        // 从第2行开始取数据,默认第一行是表头.
        Row row = sheet.getRow(i);
        // 判断当前行是否是空行
        if (isRowEmpty(row)) {
          continue;
        }
        T entity = null;
        for (Map.Entry<Integer, ExcelFieldDetail> entry : fieldsMap.entrySet()) {
          Object val = this.getCellValue(row, entry.getKey());

          // 如果不存在实例则新建.
          entity = (entity == null ? clazz.newInstance() : entity);
          // 从map中得到对应列的field.
          Field field = entry.getValue().getField();
          ExcelFieldDetail attr = entry.getValue();
          // 取得类型,并根据对象类型设置值.
          Class<?> fieldType = field.getType();
          if (String.class == fieldType) {
            String s = Convert.toStr(val);
            if (s.endsWith(".0")) {
              val = StrUtil.subBefore(s, ".0", false);
            } else {
              String dateFormat = field.getAnnotation(Excel.class).dateFormat();
              if (StrUtil.isNotBlank(dateFormat)) {
                val = DateTime.of((Date) val).toString(dateFormat);
              } else {
                val = Convert.toStr(val);
              }
            }
          } else if ((Integer.TYPE == fieldType || Integer.class == fieldType) && StrUtil.isNumeric(Convert.toStr(val))) {
            val = Convert.toInt(val);
          } else if (Long.TYPE == fieldType || Long.class == fieldType) {
            val = Convert.toLong(val);
          } else if (Double.TYPE == fieldType || Double.class == fieldType) {
            val = Convert.toDouble(val);
          } else if (Float.TYPE == fieldType || Float.class == fieldType) {
            val = Convert.toFloat(val);
          } else if (BigDecimal.class == fieldType) {
            val = Convert.toBigDecimal(val);
          } else if (Date.class == fieldType) {
            if (val instanceof String) {
              val = DateUtil.parse((CharSequence) val);
            } else if (val instanceof Double) {
              val = DateUtil.date((Long) val);
            }
          } else if (Boolean.TYPE == fieldType || Boolean.class == fieldType) {
            val = Convert.toBool(val, false);
          }
          if (ObjectUtil.isNotNull(fieldType)) {
            String propertyName = field.getName();
            if (StrUtil.isNotEmpty(attr.getTargetAttr())) {
              propertyName = field.getName() + "." + attr.getTargetAttr();
            } else if (StrUtil.isNotEmpty(attr.getReadConverterExp())) {
              val = reverseByExp(Convert.toStr(val), attr.getReadConverterExp(), attr.getSeparator());
            } else if (attr.getHandler() != null && ExcelHandlerAdapter.class.isAssignableFrom(attr.getHandler())) {
              val = dataFormatHandlerAdapter(val, attr);
            }
            ReflectUtil.setFieldValue(entity, propertyName, val);
          }
        }
        list.add(entity);
      }
    }
    return list;
  }

  /**
   * 对list数据源将其里面的数据导入到excel表单
   *
   * @param response  返回数据
   * @param list      导出数据集合
   * @param sheetName 工作表的名称
   * @return 结果
   * @throws IOException
   */
  public void exportExcel(HttpServletResponse response, List<T> list, String sheetName) {
    exportExcel(response, list, sheetName, "");
  }

  /**
   * 对list数据源将其里面的数据导入到excel表单
   *
   * @param response  返回数据
   * @param list      导出数据集合
   * @param sheetName 工作表的名称
   * @param title     标题
   * @return 结果
   * @throws IOException
   */
  public void exportExcel(HttpServletResponse response, List<T> list, String sheetName, String title) {
    response.setContentType("application/octet-stream");
    response.setHeader("Content-disposition", "attachment;filename=" + URLUtil.encode(title) + ".xlsx");
    response.setCharacterEncoding("utf-8");
    this.init(list, sheetName, title, Excel.Type.EXPORT);
    exportExcel(response);
  }

  /**
   * 对list数据源将其里面的数据导入到excel表单
   *
   * @param sheetName 工作表的名称
   * @return 结果
   */
  /**
   * 对list数据源将其里面的数据导入到excel表单
   *
   * @param sheetName 工作表的名称
   * @return 结果
   */
  public void importTemplateExcel(HttpServletResponse response, String sheetName) {
    importTemplateExcel(response, sheetName, "");
  }

  /**
   * 对list数据源将其里面的数据导入到excel表单
   *
   * @param sheetName 工作表的名称
   * @param title     标题
   * @return 结果
   */
  public void importTemplateExcel(HttpServletResponse response, String sheetName, String title) {
    response.setContentType("application/octet-stream");
    response.setHeader("Content-disposition", "attachment;filename=" + URLUtil.encode(title) + ".xlsx");
    response.setCharacterEncoding("utf-8");
    this.init(null, sheetName, title, Excel.Type.IMPORT);
    exportExcel(response);
  }

  /**
   * 对list数据源将其里面的数据导入到excel表单
   *
   * @return 结果
   */
  public void exportExcel(HttpServletResponse response) {
    try {
      writeSheet();
      wb.write(response.getOutputStream());
    } catch (Exception e) {
      log.error("导出Excel异常{}", e.getMessage(), e);
    } finally {
      if (wb != null) {
        try {
          wb.close();
        } catch (Exception var2) {
          throw ResultException.create("关闭移除");
        }

      }
    }
  }

  /**
   * 创建写入数据到Sheet
   */
  public void writeSheet() {
    // 取出一共有多少个sheet.
    int sheetNo = Math.max(1, (int) Math.ceil(list.size() * 1.0 / sheetSize));
    for (int index = 0; index < sheetNo; index++) {
      createSheet(sheetNo, index);

      // 产生一行
      Row row = sheet.createRow(rownum);
      int column = 0;
      // 写入各个字段的列头名称
      for (ExcelFieldDetail detail : fields) {
        this.createCell(detail, row, column++);
      }
      if (Excel.Type.EXPORT.equals(type)) {
        fillExcelData(index, row);
        addStatisticsRow();
      }
    }
  }

  /**
   * 填充excel数据
   *
   * @param index 序号
   * @param row   单元格行
   */
  public void fillExcelData(int index, Row row) {
    int startNo = index * sheetSize;
    int endNo = Math.min(startNo + sheetSize, list.size());
    for (int i = startNo; i < endNo; i++) {
      row = sheet.createRow(i + 1 + rownum - startNo);
      // 得到导出对象.
      T vo = list.get(i);
      int column = 0;
      for (ExcelFieldDetail detail : fields) {
        this.addCell(detail, row, vo, detail.getField(), column++);
      }
    }
  }

  /**
   * 创建表格样式
   *
   * @param wb 工作薄对象
   * @return 样式列表
   */
  private Map<String, CellStyle> createStyles(Workbook wb) {
    // 写入各条记录,每条记录对应excel表中的一行
    Map<String, CellStyle> styles = new HashMap<String, CellStyle>();
    CellStyle style = wb.createCellStyle();
    style.setAlignment(HorizontalAlignment.CENTER);
    style.setVerticalAlignment(VerticalAlignment.CENTER);
    Font titleFont = wb.createFont();
    titleFont.setFontName("Arial");
    titleFont.setFontHeightInPoints((short) 16);
    titleFont.setBold(true);
    style.setFont(titleFont);
    styles.put("title", style);

    style = wb.createCellStyle();
    style.setAlignment(HorizontalAlignment.CENTER);
    style.setVerticalAlignment(VerticalAlignment.CENTER);
    style.setBorderRight(BorderStyle.THIN);
    style.setRightBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
    style.setBorderLeft(BorderStyle.THIN);
    style.setLeftBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
    style.setBorderTop(BorderStyle.THIN);
    style.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
    style.setBorderBottom(BorderStyle.THIN);
    style.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
    Font dataFont = wb.createFont();
    dataFont.setFontName("Arial");
    dataFont.setFontHeightInPoints((short) 10);
    style.setFont(dataFont);
    styles.put("data", style);

    style = wb.createCellStyle();
    style.cloneStyleFrom(styles.get("data"));
    style.setAlignment(HorizontalAlignment.CENTER);
    style.setVerticalAlignment(VerticalAlignment.CENTER);
    style.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
    style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
    Font headerFont = wb.createFont();
    headerFont.setFontName("Arial");
    headerFont.setFontHeightInPoints((short) 10);
    headerFont.setBold(true);
    headerFont.setColor(IndexedColors.WHITE.getIndex());
    style.setFont(headerFont);
    styles.put("header", style);

    style = wb.createCellStyle();
    style.setAlignment(HorizontalAlignment.CENTER);
    style.setVerticalAlignment(VerticalAlignment.CENTER);
    Font totalFont = wb.createFont();
    totalFont.setFontName("Arial");
    totalFont.setFontHeightInPoints((short) 10);
    style.setFont(totalFont);
    styles.put("total", style);

    style = wb.createCellStyle();
    style.cloneStyleFrom(styles.get("data"));
    style.setAlignment(HorizontalAlignment.LEFT);
    styles.put("data1", style);

    style = wb.createCellStyle();
    style.cloneStyleFrom(styles.get("data"));
    style.setAlignment(HorizontalAlignment.CENTER);
    styles.put("data2", style);

    style = wb.createCellStyle();
    style.cloneStyleFrom(styles.get("data"));
    style.setAlignment(HorizontalAlignment.RIGHT);
    styles.put("data3", style);

    return styles;
  }

  /**
   * 创建单元格
   */
  public Cell createCell(ExcelFieldDetail attr, Row row, int column) {
    // 创建列
    Cell cell = row.createCell(column);
    // 写入列信息
    cell.setCellValue(attr.getFieldName());
    setDataValidation(attr, row, column);
    cell.setCellStyle(styles.get("header"));
    return cell;
  }

  /**
   * 设置单元格信息
   *
   * @param value 单元格值
   * @param attr  注解相关
   * @param cell  单元格信息
   */
  public void setCellVo(Object value, ExcelFieldDetail attr, Cell cell) {
    if (Excel.ColumnType.STRING == attr.getCellType()) {
      cell.setCellValue(Objects.isNull(value) ? attr.getDefaultValue() : value + StrUtil.nullToDefault(attr.getSuffix(), ""));
    } else if (Excel.ColumnType.NUMERIC == attr.getCellType()) {
      if (ObjectUtil.isNotNull(value)) {
        cell.setCellValue(StrUtil.contains(Convert.toStr(value), ".") ? Convert.toDouble(value) : Convert.toInt(value));
      }
    } else if (Excel.ColumnType.IMAGE == attr.getCellType()) {
      ClientAnchor anchor = new XSSFClientAnchor(0, 0, 0, 0, (short) cell.getColumnIndex(), cell.getRow().getRowNum(), (short) (cell.getColumnIndex() + 1), cell.getRow().getRowNum() + 1);
      String imagePath = Convert.toStr(value);
      if (ObjectUtil.isNotEmpty(imagePath)) {
        byte[] data = FileUtil.readBytes(imagePath);
        getDrawingPatriarch(cell.getSheet()).createPicture(anchor,
          cell.getSheet().getWorkbook().addPicture(data, getImageType(data)));
      }
    }
  }

  /**
   * 获取画布
   */
  public static Drawing<?> getDrawingPatriarch(Sheet sheet) {
    if (sheet.getDrawingPatriarch() == null) {
      sheet.createDrawingPatriarch();
    }
    return sheet.getDrawingPatriarch();
  }

  /**
   * 获取图片类型,设置图片插入类型
   */
  public int getImageType(byte[] value) {
    String type = getFileExtendName(value);
    if ("JPG".equalsIgnoreCase(type)) {
      return Workbook.PICTURE_TYPE_JPEG;
    } else if ("PNG".equalsIgnoreCase(type)) {
      return Workbook.PICTURE_TYPE_PNG;
    }
    return Workbook.PICTURE_TYPE_JPEG;
  }

  /**
   * 获取文件类型
   *
   * @param photoByte 文件字节码
   * @return 后缀（不含".")
   */
  public static String getFileExtendName(byte[] photoByte) {
    String strFileExtendName = "JPG";
    if ((photoByte[0] == 71) && (photoByte[1] == 73) && (photoByte[2] == 70) && (photoByte[3] == 56)
      && ((photoByte[4] == 55) || (photoByte[4] == 57)) && (photoByte[5] == 97)) {
      strFileExtendName = "GIF";
    } else if ((photoByte[6] == 74) && (photoByte[7] == 70) && (photoByte[8] == 73) && (photoByte[9] == 70)) {
      strFileExtendName = "JPG";
    } else if ((photoByte[0] == 66) && (photoByte[1] == 77)) {
      strFileExtendName = "BMP";
    } else if ((photoByte[1] == 80) && (photoByte[2] == 78) && (photoByte[3] == 71)) {
      strFileExtendName = "PNG";
    }
    return strFileExtendName;
  }

  /**
   * 创建表格样式
   */
  public void setDataValidation(ExcelFieldDetail attr, Row row, int column) {
    if (attr.getFieldName().contains("注：")) {
      sheet.setColumnWidth(column, 6000);
    } else {
      // 设置列宽
      sheet.setColumnWidth(column, (int) ((attr.getWidth() + 0.72) * 256));
    }
    // 如果设置了提示信息则鼠标放上去提示.
    if (StrUtil.isNotBlank(attr.getPrompt())) {
      // 这里默认设了2-101列提示.
      setXSSFPrompt(sheet, "", attr.getPrompt(), 1, 100, column, column);
    }
    // 如果设置了combo属性则本列只能选择不能输入
    if (attr.getCombo().length > 0) {
      // 这里默认设了2-101列只能选择不能输入.
      setXSSFValidation(sheet, attr.getCombo(), 1, 100, column, column);
    }
  }

  /**
   * 添加单元格
   */
  public Cell addCell(ExcelFieldDetail attr, Row row, T vo, Field field, int column) {
    Cell cell = null;
    try {
      // 设置行高
      row.setHeight(maxHeight);
      // 创建cell
      cell = row.createCell(column);
      int align = Excel.Align.AUTO.value();
      cell.setCellStyle(styles.get("data" + (align >= 1 && align <= 3 ? align : "")));

      // 用于读取对象中的属性
      Object value = getTargetValue(vo, field, attr);
      String dateFormat = StrUtil.blankToDefault(attr.getDateFormat(), this.dateFormat);
      String readConverterExp = attr.getReadConverterExp();
      String separator = attr.getSeparator();
      if (value instanceof Date && StrUtil.isNotBlank(dateFormat) && ObjectUtil.isNotNull(value)) {
        cell.setCellValue(DateTime.of((Date) value).toString(dateFormat));
      } else if (StrUtil.isNotBlank(readConverterExp) && ObjectUtil.isNotNull(value)) {
        cell.setCellValue(convertByExp(Convert.toStr(value), readConverterExp, separator));
      } else if (value instanceof BigDecimal && -1 != attr.getScale()) {
        cell.setCellValue((((BigDecimal) value).setScale(attr.getScale(), attr.getRoundingMode())).toString());
      } else if (attr.getHandler() != null && ExcelHandlerAdapter.class.isAssignableFrom(attr.getHandler())) {
        cell.setCellValue(dataFormatHandlerAdapter(value, attr));
      } else {
        // 设置列类型
        setCellVo(value, attr, cell);
      }
      addStatisticsData(column, Convert.toStr(value), attr);
    } catch (Exception e) {
      log.error("导出Excel失败{}", e);
    }
    return cell;
  }

  /**
   * 设置 POI XSSFSheet 单元格提示
   *
   * @param sheet         表单
   * @param promptTitle   提示标题
   * @param promptContent 提示内容
   * @param firstRow      开始行
   * @param endRow        结束行
   * @param firstCol      开始列
   * @param endCol        结束列
   */
  public void setXSSFPrompt(Sheet sheet, String promptTitle, String promptContent, int firstRow, int endRow,
                            int firstCol, int endCol) {
    DataValidationHelper helper = sheet.getDataValidationHelper();
    DataValidationConstraint constraint = helper.createCustomConstraint("DD1");
    CellRangeAddressList regions = new CellRangeAddressList(firstRow, endRow, firstCol, endCol);
    DataValidation dataValidation = helper.createValidation(constraint, regions);
    dataValidation.createPromptBox(promptTitle, promptContent);
    dataValidation.setShowPromptBox(true);
    sheet.addValidationData(dataValidation);
  }

  /**
   * 设置某些列的值只能输入预制的数据,显示下拉框.
   *
   * @param sheet    要设置的sheet.
   * @param textlist 下拉框显示的内容
   * @param firstRow 开始行
   * @param endRow   结束行
   * @param firstCol 开始列
   * @param endCol   结束列
   * @return 设置好的sheet.
   */
  public void setXSSFValidation(Sheet sheet, String[] textlist, int firstRow, int endRow, int firstCol, int endCol) {
    DataValidationHelper helper = sheet.getDataValidationHelper();
    // 加载下拉列表内容
    DataValidationConstraint constraint = helper.createExplicitListConstraint(textlist);
    // 设置数据有效性加载在哪个单元格上,四个参数分别是：起始行、终止行、起始列、终止列
    CellRangeAddressList regions = new CellRangeAddressList(firstRow, endRow, firstCol, endCol);
    // 数据有效性对象
    DataValidation dataValidation = helper.createValidation(constraint, regions);
    // 处理Excel兼容性问题
    if (dataValidation instanceof XSSFDataValidation) {
      dataValidation.setSuppressDropDownArrow(true);
      dataValidation.setShowErrorBox(true);
    } else {
      dataValidation.setSuppressDropDownArrow(false);
    }

    sheet.addValidationData(dataValidation);
  }

  /**
   * 解析导出值 0=男,1=女,2=未知
   *
   * @param propertyValue 参数值
   * @param converterExp  翻译注解
   * @param separator     分隔符
   * @return 解析后值
   */
  public static String convertByExp(String propertyValue, String converterExp, String separator) {
    StringBuilder propertyString = new StringBuilder();
    String[] convertSource = converterExp.split(",");
    for (String item : convertSource) {
      String[] itemArray = item.split("=");
      if (StrUtil.containsAny(separator, propertyValue)) {
        for (String value : propertyValue.split(separator)) {
          if (itemArray[0].equals(value)) {
            propertyString.append(itemArray[1] + separator);
            break;
          }
        }
      } else {
        if (itemArray[0].equals(propertyValue)) {
          return itemArray[1];
        }
      }
    }
    return StrUtil.strip(propertyString.toString(), null, separator);
  }

  /**
   * 反向解析值 男=0,女=1,未知=2
   *
   * @param propertyValue 参数值
   * @param converterExp  翻译注解
   * @param separator     分隔符
   * @return 解析后值
   */
  public static String reverseByExp(String propertyValue, String converterExp, String separator) {
    StringBuilder propertyString = new StringBuilder();
    String[] convertSource = converterExp.split(",");
    for (String item : convertSource) {
      String[] itemArray = item.split("=");
      if (StrUtil.containsAny(separator, propertyValue)) {
        for (String value : propertyValue.split(separator)) {
          if (itemArray[1].equals(value)) {
            propertyString.append(itemArray[0] + separator);
            break;
          }
        }
      } else {
        if (itemArray[1].equals(propertyValue)) {
          return itemArray[0];
        }
      }
    }
    return StrUtil.strip(propertyString.toString(), null, separator);
  }

  /**
   * 数据处理器
   *
   * @param value 数据值
   * @param excel 数据注解
   * @return
   */
  public String dataFormatHandlerAdapter(Object value, ExcelFieldDetail excel) {
    try {
      ExcelHandlerAdapter adapter = excel.getHandler().newInstance();
      value = adapter.format(value, excel);
    } catch (Exception e) {
      log.error("不能格式化数据 " + excel.getHandler(), e.getMessage());
    }
    return Convert.toStr(value);
  }

  /**
   * 合计统计信息
   */
  private void addStatisticsData(Integer index, String text, ExcelFieldDetail entity) {
    if (entity != null && entity.isStatistics()) {
      Double temp = 0D;
      if (!statistics.containsKey(index)) {
        statistics.put(index, temp);
      }
      try {
        temp = Double.valueOf(text);
      } catch (NumberFormatException e) {
      }
      statistics.put(index, statistics.get(index) + temp);
    }
  }

  /**
   * 创建统计行
   */
  public void addStatisticsRow() {
    if (statistics.size() > 0) {
      Row row = sheet.createRow(sheet.getLastRowNum() + 1);
      Set<Integer> keys = statistics.keySet();
      Cell cell = row.createCell(0);
      cell.setCellStyle(styles.get("total"));
      cell.setCellValue("合计");

      for (Integer key : keys) {
        cell = row.createCell(key);
        cell.setCellStyle(styles.get("total"));
        cell.setCellValue(DOUBLE_FORMAT.format(statistics.get(key)));
      }
      statistics.clear();
    }
  }

  /**
   * 获取bean中的属性值
   *
   * @param vo     实体对象
   * @param field  字段
   * @param detail 注解
   * @return 最终的属性值
   * @throws Exception
   */
  private Object getTargetValue(T vo, Field field, ExcelFieldDetail detail) throws Exception {
    Object o = field.get(vo);
    if (StrUtil.isNotBlank(detail.getTargetAttr())) {
      String target = detail.getTargetAttr();
      if (target.contains(".")) {
        String[] targets = target.split("[.]");
        for (String name : targets) {
          o = getValue(o, name);
        }
      } else {
        o = getValue(o, target);
      }
    }
    return o;
  }

  /**
   * 以类的属性的get方法方法形式获取值
   *
   * @param o
   * @param name
   * @return value
   * @throws Exception
   */
  private Object getValue(Object o, String name) throws Exception {
    if (StrUtil.isNotBlank(name)) {
      Class<?> clazz = o.getClass();
      Field field = clazz.getDeclaredField(name);
      field.setAccessible(true);
      o = field.get(o);
    }
    return o;
  }

  /**
   * 得到所有定义字段
   */
  private void createExcelField() {
    this.fields = getFields();
    //排序
    this.fields = this.fields.stream().sorted(Comparator.comparing(ExcelFieldDetail::getSort)).collect(Collectors.toList());
    this.maxHeight = getRowHeight();
  }

  /**
   * 获取字段注解信息
   */
  public List<ExcelFieldDetail> getFields() {
    List<ExcelFieldDetail> fields = new ArrayList<>();
    List<Field> tempFields = new ArrayList<>();
    tempFields.addAll(Arrays.asList(clazz.getDeclaredFields()));
    List<Field> superField = Arrays.asList(clazz.getSuperclass().getDeclaredFields());
    if (CollUtil.isNotEmpty(superField)) {
      if (CollUtil.isEmpty(tempFields)) {
        tempFields.addAll(superField);
      } else {
        Set<String> fieldNames = tempFields.stream().map(Field::getName).collect(Collectors.toSet());
        for (Field field : superField) {
          if (!fieldNames.contains(field.getName())) {
            tempFields.add(field);
          }
        }
      }
    }
    for (Field field : tempFields) {
      // 单注解
      if (field.isAnnotationPresent(Excel.class)) {
        Excel attr = field.getAnnotation(Excel.class);
        if (attr != null && (attr.type() == Excel.Type.ALL || attr.type() == type)) {
          field.setAccessible(true);
          ExcelFieldDetail detail = new ExcelFieldDetail();
          detail.setField(field);
          detail.setExcel(attr);
          fields.add(detail);
        }
      } else if (CollUtil.isNotEmpty(fieldFunctions)) {
        for (Function<Field, ExcelFieldDetail> function : fieldFunctions) {
          ExcelFieldDetail detail = function.apply(field);
          if (detail != null) {
            fields.add(detail);
            break;
          }
        }
      }
    }
    return fields;
  }

  /**
   * 根据注解获取最大行高
   */
  public short getRowHeight() {
    double maxHeight = 0;
    for (ExcelFieldDetail os : this.fields) {
      maxHeight = maxHeight > os.getHeight() ? maxHeight : os.getHeight();
    }
    return (short) (maxHeight * 20);
  }

  /**
   * 创建一个工作簿
   */
  public void createWorkbook() {
    this.wb = new SXSSFWorkbook(500);
    this.sheet = wb.createSheet();
    wb.setSheetName(0, sheetName);
    this.styles = createStyles(wb);
  }

  /**
   * 创建工作表
   *
   * @param sheetNo sheet数量
   * @param index   序号
   */
  public void createSheet(int sheetNo, int index) {
    // 设置工作表的名称.
    if (sheetNo > 1 && index > 0) {
      this.sheet = wb.createSheet();
      this.createTitle();
      wb.setSheetName(index, sheetName + index);
    }
  }

  /**
   * 获取单元格值
   *
   * @param row    获取的行
   * @param column 获取单元格列号
   * @return 单元格值
   */
  public Object getCellValue(Row row, int column) {
    if (row == null) {
      return row;
    }
    Object val = "";
    try {
      Cell cell = row.getCell(column);
      if (ObjectUtil.isNotNull(cell)) {
        if (cell.getCellType() == CellType.NUMERIC || cell.getCellType() == CellType.FORMULA) {
          val = cell.getNumericCellValue();
          if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
            val = DateUtil.parse((CharSequence) val); // POI Excel 日期格式转换
          } else {
            if ((Double) val % 1 != 0) {
              val = new BigDecimal(val.toString());
            } else {
              val = new DecimalFormat("0").format(val);
            }
          }
        } else if (cell.getCellType() == CellType.STRING) {
          val = cell.getStringCellValue();
        } else if (cell.getCellType() == CellType.BOOLEAN) {
          val = cell.getBooleanCellValue();
        } else if (cell.getCellType() == CellType.ERROR) {
          val = cell.getErrorCellValue();
        }

      }
    } catch (Exception e) {
      return val;
    }
    return val;
  }

  /**
   * 判断是否是空行
   *
   * @param row 判断的行
   * @return
   */
  private boolean isRowEmpty(Row row) {
    if (row == null) {
      return true;
    }
    for (int i = row.getFirstCellNum(); i < row.getLastCellNum(); i++) {
      Cell cell = row.getCell(i);
      if (cell != null && cell.getCellType() != CellType.BLANK) {
        return false;
      }
    }
    return true;
  }
}