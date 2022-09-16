package top.mrys.custom;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ReflectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import top.mrys.core.PageParam;
import top.mrys.core.PageResult;
import top.mrys.core.ResultException;

/**
 * @author mrys
 */
public class BaseServiceImpl<M extends BaseMapper<T>, T> implements BaseService<T> {

  @Autowired
  protected M baseMapper;

  @Override
  public PageResult<T> pageResult(PageParam pageParam, T t) {
    Page<T> page = baseMapper.selectPage(PageDTO.of(pageParam.getCurrent(), pageParam.getSize()),
      new QueryWrapper<>(t));
    return toPageResult(page);
  }

  @Override
  public T getById(Serializable id) {
    return baseMapper.selectById(id);
  }


  @Override
  public Boolean insert(T t) {
    if (checkExist(t)) {
      //存在
      return false;
    }
    fillData(t, EnumActionType.INSERT);
    return 1 == baseMapper.insert(t);
  }

  protected void fillData(T t, EnumActionType type) {
    TableInfo tableInfo = TableInfoHelper.getTableInfo(t.getClass());
    tableInfo.getFieldList().stream()
      .filter(column -> column.getField().getAnnotation(DBFill.class) != null)
      .filter(
        column -> ArrayUtil.contains(column.getField().getAnnotation(DBFill.class).types(),
          type))
      .forEach(column -> {
        Object value = ReflectUtil.getFieldValue(t, column.getProperty());
        if (Objects.isNull(value)) {
          ReflectUtil.setFieldValue(t, column.getProperty(),
            column.getField().getAnnotation(DBFill.class).value()..get());
        }
      });

  }

  protected boolean checkExist(T t) {
    TableInfo tableInfo = TableInfoHelper.getTableInfo(t.getClass());
    String idColumn = tableInfo.getKeyColumn();
    Map<String, String> names = new HashMap<>();
    List<TableFieldInfo> uniqueColumns = tableInfo.getFieldList().stream()
      .filter(column -> column.getField().getAnnotation(DBUnique.class) != null)
      .peek(column -> names.put(column.getColumn(),
        column.getField().getAnnotation(DBUnique.class).value()))
      .collect(Collectors.toList());
    if (CollUtil.isNotEmpty(uniqueColumns)) {
      long count = uniqueColumns.stream()
        .map(column -> ReflectUtil.getFieldValue(t, column.getProperty()))
        .filter(Objects::nonNull)
        .count();
      if (count > 0) {
        QueryWrapper<T> wrapper = new QueryWrapper<>();
        Object idValue = ReflectUtil.getFieldValue(t, tableInfo.getKeyProperty());
        if (Objects.nonNull(idValue)) {
          wrapper.ne(idColumn, idValue);
        }
        StringBuilder sb = new StringBuilder();
        wrapper.and(w -> uniqueColumns.forEach(uniqueColumn -> {
          Object fieldValue = ReflectUtil.getFieldValue(t, uniqueColumn.getProperty());
          boolean condition = Objects.nonNull(fieldValue);
          if (condition) {
            sb.append(names.get(uniqueColumn.getColumn())).append(", ");
            w.or().eq(uniqueColumn.getColumn(), fieldValue);
          }
        }));
        if (baseMapper.selectCount(wrapper) > 0) {
          sb.append("已存在重复");
          throw ResultException.create(sb.toString());
        }
      }
    }
    return false;
  }

  @Override
  public Boolean updateById(T t) {
    customUpdate(t);
    if (checkExist(t)) {
      //存在
      return false;
    }
    fillData(t, EnumActionType.UPDATE);
    return 1 == baseMapper.updateById(t);
  }

  protected void customUpdate(T t) {

  }

  @Override
  public Integer delByIds(Serializable[] ids) {
    return baseMapper.deleteBatchIds(CollUtil.newHashSet(ids));
  }
}
