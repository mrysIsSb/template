package top.mrys.example.controller;


import top.mrys.example.service.SysLoginPasswordService;
import top.mrys.example.service.SysLoginPasswordService.SysLoginPasswordInfo;
import top.mrys.example.service.SysLoginPasswordService.SearchSysLoginPassword;
import top.mrys.example.service.SysLoginPasswordService.EditSysLoginPassword;
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
 * 用户密码登录(SysLoginPassword)controller
 *
 * @author mrys
 */
@RestController
@RequestMapping("/sysLoginPassword")
@Tag(name = "用户密码登录")
public class SysLoginPasswordController {

  private static final String sourceName = "sysLoginPassword";

  @Autowired
  private SysLoginPasswordService sysLoginPasswordService;

  @Operation(summary = "列表", description = "列表查询")
  @HasPermission(sourceName + ":list")
  @GetMapping("/list")
  public PageResult<SysLoginPasswordInfo> list(SearchSysLoginPassword search, PageParam pageParam) {
    return sysLoginPasswordService.list(search, pageParam);
  }

  @Operation(summary = "新增", description = "添加")
  @HasPermission(sourceName + ":add")
  @PostMapping("/add")
  public Result<EditSysLoginPassword> add(@Validated(Group.Add.class) @RequestBody EditSysLoginPassword edit) {
    return sysLoginPasswordService.add(edit);
  }

  @Operation(summary = "修改", description = "修改")
  @HasPermission(sourceName + ":edit")
  @PutMapping("/edit")
  public Result<EditSysLoginPassword> edit(@Validated(Group.Edit.class) @RequestBody EditSysLoginPassword edit) {
    return sysLoginPasswordService.edit(edit);
  }


  @Operation(summary = "详情", description = "获取详情")
  @HasPermission(sourceName + ":info")
  @GetMapping("{id}")
  public Result<SysLoginPasswordInfo> info(@Validated @NotNull @PathVariable Integer id) {
    return sysLoginPasswordService.info(id);
  }

  @Operation(summary = "删除", description = "删除")
  @HasPermission(sourceName + ":del")
  @DeleteMapping("{ids}")
  public Result<Boolean> del(@Validated @NotNull @PathVariable Integer[] ids) {
    return sysLoginPasswordService.del(ids);
  }

}

