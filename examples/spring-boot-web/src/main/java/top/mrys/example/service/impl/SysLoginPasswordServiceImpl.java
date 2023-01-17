package top.mrys.example.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import top.mrys.core.PageParam;
import top.mrys.core.PageResult;
import top.mrys.core.Result;
import top.mrys.custom.BaseServiceImpl;
import top.mrys.example.domain.SysLoginPassword;
import top.mrys.example.mapper.SysLoginPasswordMapper;
import top.mrys.example.service.SysLoginPasswordService;

import java.util.stream.Collectors;


/**
 * 用户密码登录(SysLoginPassword)表服务实现类
 *
 * @author mrys
 */
@Service("sysLoginPasswordService")
public class SysLoginPasswordServiceImpl extends BaseServiceImpl<SysLoginPasswordMapper, SysLoginPassword> implements SysLoginPasswordService {
  @Override
  public PageResult<SysLoginPasswordInfo> list(SearchSysLoginPassword search, PageParam pageParam) {
    Page<SysLoginPassword> page = baseMapper.selectPage((Page<SysLoginPassword>) pageParam.convert(IPage.class), null);
    Page<SysLoginPasswordInfo> page1 = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
    page1.setRecords(page.getRecords().stream().map(a -> {
      SysLoginPasswordInfo info = new SysLoginPasswordInfo();
      BeanUtil.copyProperties(a, info);
      return info;
    }).collect(Collectors.toList()));
    return toPageResult(page1);
  }

  @Override
  public Result<EditSysLoginPassword> add(EditSysLoginPassword edit) {
    super.insert(edit);
    return Result.success(edit);
  }

  @Override
  public Result<EditSysLoginPassword> edit(EditSysLoginPassword edit) {
    super.updateById(edit);
    return Result.success(edit);
  }

  @Override
  public Result<SysLoginPasswordInfo> info(Integer id) {
    SysLoginPassword data = super.getById(id);
    SysLoginPasswordInfo info = new SysLoginPasswordInfo();
    BeanUtil.copyProperties(data, info);
    return Result.success(info);
  }

  @Override
  public Result<Boolean> del(Integer[] ids) {
    Integer i = super.delByIds(ids);
    return Result.assert0(ids.length == i, "成功:" + i + ",失败:" + (ids.length - i), true);
  }
}

