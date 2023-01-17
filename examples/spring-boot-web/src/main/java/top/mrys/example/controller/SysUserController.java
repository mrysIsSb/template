package top.mrys.example.controller;


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
import top.mrys.example.service.SysUserService;
import top.mrys.example.service.SysUserService.EditSysUser;
import top.mrys.example.service.SysUserService.SearchSysUser;
import top.mrys.example.service.SysUserService.SysUserInfo;

/**
 * 系统用户表(SysUser)controller
 *
 * @author mrys
 */
@RestController
@RequestMapping("/sysUser")
@Tag(name = "系统用户表")
public class SysUserController {

  private static final String sourceName = "sysUser";

  @Autowired
  private SysUserService sysUserService;

  @Operation(summary = "列表", description = "列表查询")
  @HasPermission(sourceName + ":list")
  @GetMapping("/list")
  public PageResult<SysUserInfo> list(SearchSysUser search, PageParam pageParam) {
    return sysUserService.list(search, pageParam);
  }

  @Operation(summary = "新增", description = "添加")
  @HasPermission(sourceName + ":add")
  @PostMapping("/add")
  public Result<EditSysUser> add(@Validated(Group.Add.class) @RequestBody EditSysUser edit) {
    return sysUserService.add(edit);
  }

  @Operation(summary = "修改", description = "修改")
  @HasPermission(sourceName + ":edit")
  @PutMapping("/edit")
  public Result<EditSysUser> edit(@Validated(Group.Edit.class) @RequestBody EditSysUser edit) {
    return sysUserService.edit(edit);
  }

  @Operation(summary = "详情", description = "获取详情")
  @HasPermission(sourceName + ":info")
  @GetMapping("{id}")
  public Result<SysUserInfo> info(@Validated @NotNull @PathVariable String id) {
    return sysUserService.info(id);
  }

  @Operation(summary = "删除", description = "删除")
  @HasPermission(sourceName + ":del")
  @DeleteMapping("{ids}")
  public Result<Boolean> del(@Validated @NotNull @PathVariable String[] ids) {
    return sysUserService.del(ids);
  }

}

