package top.mrys.example.service;


import top.mrys.example.domain.TenantRole;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import top.mrys.core.PageParam;
import top.mrys.core.PageResult;
import top.mrys.core.Result;
import top.mrys.custom.BaseService;

/**
 * 租户角色表(TenantRole)表服务接口
 *
 * @author mrys
 */
public interface TenantRoleService extends BaseService<TenantRole> {
  PageResult<TenantRoleInfo> list(SearchTenantRole search, PageParam pageParam);

  Result<EditTenantRole> add(EditTenantRole edit);

  Result<EditTenantRole> edit(EditTenantRole edit);

  Result<TenantRoleInfo> info(String id);

  Result<Boolean> del(String[] ids);

  @Setter
  @Getter
  @Accessors(chain = true)
  @ToString
  class SearchTenantRole {

  }

  @Setter
  @Getter
  @ToString
  @Accessors(chain = true)
  class EditTenantRole extends TenantRole {

  }

  @Setter
  @Getter
  @Accessors(chain = true)
  @ToString
  class TenantRoleInfo extends TenantRole {

  }
}

