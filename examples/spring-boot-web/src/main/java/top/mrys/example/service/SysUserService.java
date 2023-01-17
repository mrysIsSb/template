package top.mrys.example.service;


import top.mrys.example.domain.SysUser;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import top.mrys.core.PageParam;
import top.mrys.core.PageResult;
import top.mrys.core.Result;
import top.mrys.custom.BaseService;

/**
 * 系统用户表(SysUser)表服务接口
 *
 * @author mrys
 */
public interface SysUserService extends BaseService<SysUser> {
  PageResult<SysUserInfo> list(SearchSysUser search, PageParam pageParam);

  Result<EditSysUser> add(EditSysUser edit);

  Result<EditSysUser> edit(EditSysUser edit);

  Result<SysUserInfo> info(String id);

  Result<Boolean> del(String[] ids);

  @Setter
  @Getter
  @Accessors(chain = true)
  @ToString
  class SearchSysUser {

  }

  @Setter
  @Getter
  @ToString
  @Accessors(chain = true)
  class EditSysUser extends SysUser {

  }

  @Setter
  @Getter
  @Accessors(chain = true)
  @ToString
  class SysUserInfo extends SysUser {

  }
}

