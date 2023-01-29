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
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotatedElementUtils;
import top.mrys.core.PageParam;
import top.mrys.core.PageResult;
import top.mrys.core.ResultException;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author mrys
 */
public class BaseServiceImpl<M extends BaseMapper<T>, T> implements BaseService<T> {

  @Autowired
  protected M baseMapper;

  @Autowired
  private ApplicationContext applicationContext;

  protected Logger log() {
    return org.slf4j.LoggerFactory.getLogger(this.getClass());
  }

  @Override
  public PageResult<T> pageResult(PageParam pageParam, T t) {
    Page<T> p = PageDTO.of(pageParam.getCurrent(), pageParam.getSize());
    QueryWrapper<T> queryWrapper = new QueryWrapper<>(t);
    customBeforePageResult(p, queryWrapper);
    Page<T> page = baseMapper.selectPage(p, new QueryWrapper<>(t));
    return toPageResult(page);
  }

  protected void customBeforePageResult(Page<T> page, QueryWrapper<T> qw) {
    log().trace("查询数据前的自定义操作");
  }

  @Override
  public T getById(Serializable id) {
    return baseMapper.selectById(id);
  }

  @Override
  public Boolean insert(T t) {
    //判断是否存在
    if (checkExist(t)) {
      //存在
      return false;
    }
    fillData(t, EnumActionType.INSERT);

    if (1 == baseMapper.insert(t)) {
      //插入成功
      if (AnnotatedElementUtils.hasAnnotation(t.getClass(), DBEvent.class)) {
        DBEvent dbEvent = AnnotatedElementUtils.findMergedAnnotation(t.getClass(), DBEvent.class);
        if (ArrayUtil.contains(dbEvent.value(), EnumActionType.INSERT)) {
          TableInfo tableInfo = TableInfoHelper.getTableInfo(t.getClass());

          T d = baseMapper.selectById((Serializable) ReflectUtil.getFieldValue(t, tableInfo.getKeyProperty()));
          //发送事件
          //事务有问题 todo 事务提交后才发送事件
          applicationContext.publishEvent(new DBDataEvent.Insert(tableInfo, d));
        }
      }
      return true;
    }
    return false;
  }

  protected void fillData(T t, EnumActionType type) {
    TableInfo tableInfo = TableInfoHelper.getTableInfo(t.getClass());
    tableInfo.getFieldList().stream().filter(column -> AnnotatedElementUtils.hasAnnotation(column.getField(), DBFill.class)).filter(column -> ArrayUtil.contains(Objects.requireNonNull(AnnotatedElementUtils.findMergedAnnotation(column.getField(), DBFill.class)).types(), type)).forEach(column -> {
      Object value = ReflectUtil.getFieldValue(t, column.getProperty());
      if (Objects.isNull(value)) {
        DBFillData dbFillData = SpringTool.getBean(Objects.requireNonNull(AnnotatedElementUtils.findMergedAnnotation(column.getField(), DBFill.class)).value());
        dbFillData.fill(t, column.getField(), type);
      }
    });
  }

  /**
   * 检查是否存在
   */
  protected boolean checkExist(T t) {
    TableInfo tableInfo = TableInfoHelper.getTableInfo(t.getClass());
    //获取主键
    String idColumn = tableInfo.getKeyColumn();
    Map<String, String> names = new HashMap<>();
    List<TableFieldInfo> uniqueColumns = tableInfo.getFieldList().stream()
      //过滤唯一字段
      .filter(column -> AnnotatedElementUtils.hasAnnotation(column.getField(), DBUnique.class)).peek(column -> names.put(column.getColumn(), Objects.requireNonNull(AnnotatedElementUtils.findMergedAnnotation(column.getField(), DBUnique.class)).value())).collect(Collectors.toList());
    //如果没有唯一字段
    if (CollUtil.isEmpty(uniqueColumns)) {
      return false;
    }
    //有唯一字段
    long count = uniqueColumns.stream().map(column -> ReflectUtil.getFieldValue(t, column.getProperty())).filter(Objects::nonNull).count();
    // 有值
    if (count > 0) {
      QueryWrapper<T> wrapper = new QueryWrapper<>();
      Object idValue = ReflectUtil.getFieldValue(t, tableInfo.getKeyProperty());
      if (Objects.nonNull(idValue)) {
        //id不等于
        wrapper.ne(idColumn, idValue);
      }
      StringBuilder sb = new StringBuilder();
      wrapper.and(w -> uniqueColumns.forEach(uniqueColumn -> {
        Object fieldValue = ReflectUtil.getFieldValue(t, uniqueColumn.getProperty());
        boolean condition = Objects.nonNull(fieldValue);
        if (condition) {
          sb.append(names.get(uniqueColumn.getColumn())).append(", ");
          //or 条件
          w.or().eq(uniqueColumn.getColumn(), fieldValue);
        }
      }));
      if (baseMapper.selectCount(wrapper) > 0) {
        sb.append("已存在重复");
        throw ResultException.create(sb.toString());
      }
    }
    return false;
  }

  /**
   * 根据id来修改
   *
   * @author mrys
   */
  @Override
  public Boolean updateById(T t) {
    customUpdate(t);
    if (checkExist(t)) {
      return false;
    }
    fillData(t, EnumActionType.UPDATE);
    TableInfo tableInfo = TableInfoHelper.getTableInfo(t.getClass());
    T old = AnnotatedElementUtils.hasAnnotation(t.getClass(), DBEvent.class) ? baseMapper.selectById((Serializable) ReflectUtil.getFieldValue(t, tableInfo.getKeyProperty())) : null;
    if (1 == baseMapper.updateById(t)) {
      //插入成功
      if (AnnotatedElementUtils.hasAnnotation(t.getClass(), DBEvent.class)) {
        DBEvent dbEvent = AnnotatedElementUtils.findMergedAnnotation(t.getClass(), DBEvent.class);
        if (ArrayUtil.contains(dbEvent.value(), EnumActionType.UPDATE)) {

          T d = baseMapper.selectById((Serializable) ReflectUtil.getFieldValue(t, tableInfo.getKeyProperty()));
          //发送事件
          //事务有问题 todo 事务提交后才发送事件
          applicationContext.publishEvent(new DBDataEvent.Update(tableInfo, d, old));
        }
      }
      return true;
    }
    return false;
  }

  /**
   * 自定义修改参数
   *
   * @author mrys
   */
  protected void customUpdate(T t) {

  }

  @Override
  public Integer delByIds(Serializable[] ids) {
    //删除事件
    return baseMapper.deleteBatchIds(CollUtil.newHashSet(ids));
  }
}
