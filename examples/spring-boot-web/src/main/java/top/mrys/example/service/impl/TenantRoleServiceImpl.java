package top.mrys.example.service.impl;

import top.mrys.example.mapper.TenantRoleMapper;
import top.mrys.example.domain.TenantRole;
import top.mrys.example.service.TenantRoleService;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import org.springframework.stereotype.Service;
import top.mrys.core.PageParam;
import top.mrys.core.PageResult;
import top.mrys.core.Result;
import top.mrys.custom.BaseServiceImpl;

import java.util.stream.Collectors;


/**
 * 租户角色表(TenantRole)表服务实现类
 *
 * @author mrys
 */
@Service("tenantRoleService")
public class TenantRoleServiceImpl extends BaseServiceImpl<TenantRoleMapper, TenantRole> implements TenantRoleService {
  @Override
  public PageResult<TenantRoleInfo> list(SearchTenantRole search, PageParam pageParam) {
    Page<TenantRole> page = baseMapper.selectPage((Page<TenantRole>) pageParam.convert(IPage.class), null);
    Page<TenantRoleInfo> page1 = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
    page1.setRecords(page.getRecords().stream().map(a -> {
      TenantRoleInfo info = new TenantRoleInfo();
      BeanUtil.copyProperties(a, info);
      return info;
    }).collect(Collectors.toList()));
    return toPageResult(page1);
  }

  @Override
  public Result<EditTenantRole> add(EditTenantRole edit) {
    super.insert(edit);
    return Result.success(edit);
  }

  @Override
  public Result<EditTenantRole> edit(EditTenantRole edit) {
    super.updateById(edit);
    return Result.success(edit);
  }

  @Override
  public Result<TenantRoleInfo> info(String id) {
    TenantRole data = super.getById(id);
    if (data == null) {
      return Result.fail("数据不存在");
    }
    TenantRoleInfo info = new TenantRoleInfo();
    BeanUtil.copyProperties(data, info);
    return Result.success(info);
  }

  @Override
  public Result<Boolean> del(String[] ids) {
    Integer i = super.delByIds(ids);
    return Result.assert0(ids.length == i, "成功:" + i + ",失败:" + (ids.length - i), true);
  }
}

