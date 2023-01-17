package top.mrys.example.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import top.mrys.core.PageParam;
import top.mrys.core.PageResult;
import top.mrys.core.Result;
import top.mrys.custom.BaseServiceImpl;
import top.mrys.example.domain.SysUser;
import top.mrys.example.mapper.SysUserMapper;
import top.mrys.example.service.SysUserService;

import java.util.stream.Collectors;


/**
 * 系统用户表(SysUser)表服务实现类
 *
 * @author mrys
 */
@Service("sysUserService")
public class SysUserServiceImpl extends BaseServiceImpl<SysUserMapper, SysUser> implements SysUserService {
  @Override
  public PageResult<SysUserInfo> list(SearchSysUser search, PageParam pageParam) {
    Page<SysUser> page = baseMapper.selectPage((Page<SysUser>) pageParam.convert(IPage.class), null);
    Page<SysUserInfo> page1 = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
    page1.setRecords(page.getRecords().stream().map(a -> {
      SysUserInfo info = new SysUserInfo();
      BeanUtil.copyProperties(a, info);
      return info;
    }).collect(Collectors.toList()));
    return toPageResult(page1);
  }

  @Override
  public Result<EditSysUser> add(EditSysUser edit) {
    super.insert(edit);
    return Result.success(edit);
  }

  @Override
  public Result<EditSysUser> edit(EditSysUser edit) {
    super.updateById(edit);
    return Result.success(edit);
  }

  @Override
  public Result<SysUserInfo> info(String id) {
    SysUser data = super.getById(id);
    if (data == null) {
      return Result.fail("数据不存在");
    }
    SysUserInfo info = new SysUserInfo();
    BeanUtil.copyProperties(data, info);
    return Result.success(info);
  }

  @Override
  public Result<Boolean> del(String[] ids) {
    Integer i = super.delByIds(ids);
    return Result.assert0(ids.length == i, "成功:" + i + ",失败:" + (ids.length - i), true);
  }
}

