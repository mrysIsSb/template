package top.mrys.example.controller;


import top.mrys.example.service.TenantRoleService;
import top.mrys.example.service.TenantRoleService.TenantRoleInfo;
import top.mrys.example.service.TenantRoleService.SearchTenantRole;
import top.mrys.example.service.TenantRoleService.EditTenantRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import top.mrys.core.PageParam;
import top.mrys.core.PageResult;
import top.mrys.core.Result;
import top.mrys.custom.Group;
import top.mrys.custom.annotations.HasPermission;

/**
 * 租户角色表(TenantRole)controller
 *
 * @author mrys
 */
@RestController
@RequestMapping("/tenantRole")
@Tag(name = "租户角色表")
public class TenantRoleController {

  private static final String sourceName = "tenantRole";

  @Autowired
  private TenantRoleService tenantRoleService;

  @Operation(summary = "列表", description = "列表查询")
  @HasPermission(sourceName + ":list")
  @GetMapping("/list")
  public PageResult<TenantRoleInfo> list(SearchTenantRole search, PageParam pageParam) {
    return tenantRoleService.list(search, pageParam);
  }

  @Operation(summary = "新增", description = "添加")
  @HasPermission(sourceName + ":add")
  @PostMapping("/add")
  public Result<EditTenantRole> add(@Validated(Group.Add.class) @RequestBody EditTenantRole edit) {
    return tenantRoleService.add(edit);
  }

  @Operation(summary = "修改", description = "修改")
  @HasPermission(sourceName + ":edit")
  @PutMapping("/edit")
  public Result<EditTenantRole> edit(@Validated(Group.Edit.class) @RequestBody EditTenantRole edit) {
    return tenantRoleService.edit(edit);
  }


  @Operation(summary = "详情", description = "获取详情")
  @HasPermission(sourceName + ":info")
  @GetMapping("{id}")
  public Result<TenantRoleInfo> info(@Validated @NotNull @PathVariable String id) {
    return tenantRoleService.info(id);
  }

  @Operation(summary = "删除", description = "删除")
  @HasPermission(sourceName + ":del")
  @DeleteMapping("{ids}")
  public Result<Boolean> del(@Validated @NotNull @PathVariable String[] ids) {
    return tenantRoleService.del(ids);
  }

}

