package top.mrys.example.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.mrys.core.Result;
import top.mrys.custom.annotations.Anno;
import top.mrys.example.domain.SysUser;

import java.util.List;
import java.util.Map;

/**
 * @author mrys
 */
@Anno
@RestController
@Tag(name = "index", description = "index")
public class IndexController {

  @Value("${version:1.0}")
  private String version;

  @GetMapping("/")
  public Result<String> index() {
    return Result.success("hello world! ");
  }

  @GetMapping("/info")
  @Operation(summary = "获取服务信息")
  public Result<?> info(@RequestParam(required = false) String name) {
    JSONObject info = JSONUtil.createObj()
      .set("version", version);
    if (StrUtil.isBlank(name)) {
      return Result.success(info);
    }
    return Result.success(info.get(name));
  }

  @PostMapping("/user/{uid}")
  @Operation(summary = "获取用户信息")
  public Result<SysUser> user(@RequestParam(required = false) @Parameter(description = "姓名") String name,
                              @PathVariable @Parameter(description = "用户id") String uid,
                              @RequestBody SysUser user) {
    return Result.success(new SysUser());
  }

  @PostMapping("/user1")
  @Operation(summary = "获取用户信息")
  public Result<SysUser> user1(@RequestBody List<SysUser> user) {
    return Result.success(new SysUser());
  }

  @PostMapping("/user2")
  @Operation(summary = "获取用户信息")
  public Result<List<SysUser>> user2(@RequestBody SysUser[] user) {
    return Result.success();
  }

  @PostMapping("/file")
  @Operation(summary = "上传文件")
  public Result<Map<String,List<SysUser>>> file(@RequestPart("file") MultipartFile file) {
    return Result.success();
  }

}
