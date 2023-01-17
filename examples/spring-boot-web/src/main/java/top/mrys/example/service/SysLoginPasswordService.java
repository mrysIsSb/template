package top.mrys.example.service;


import top.mrys.example.domain.SysLoginPassword;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import top.mrys.core.PageParam;
import top.mrys.core.PageResult;
import top.mrys.core.Result;
import top.mrys.custom.BaseService;

/**
 * 用户密码登录(SysLoginPassword)表服务接口
 *
 * @author mrys
 */
public interface SysLoginPasswordService extends BaseService<SysLoginPassword> {
  PageResult<SysLoginPasswordInfo> list(SearchSysLoginPassword search, PageParam pageParam);

  Result<EditSysLoginPassword> add(EditSysLoginPassword edit);

  Result<EditSysLoginPassword> edit(EditSysLoginPassword edit);

  Result<SysLoginPasswordInfo> info(Integer id);

  Result<Boolean> del(Integer[] ids);

  @Setter
  @Getter
  @Accessors(chain = true)
  @ToString
  class SearchSysLoginPassword {

  }

  @Setter
  @Getter
  @ToString
  @Accessors(chain = true)
  class EditSysLoginPassword extends SysLoginPassword {

  }

  @Setter
  @Getter
  @Accessors(chain = true)
  @ToString
  class SysLoginPasswordInfo extends SysLoginPassword {

  }
}

