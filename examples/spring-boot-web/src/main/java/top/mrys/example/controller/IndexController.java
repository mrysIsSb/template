package top.mrys.example.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.mrys.core.Result;
import top.mrys.custom.annotations.Anno;

/**
 * @author mrys
 */
@Anno
@RestController
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

}
