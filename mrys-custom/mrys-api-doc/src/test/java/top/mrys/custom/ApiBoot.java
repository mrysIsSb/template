package top.mrys.custom;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.json.JSONUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import lombok.Getter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;
import top.mrys.core.Result;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author mrys
 */
@SpringBootApplication
@Tag(name = "自定义模块测试")
@RestController
public class ApiBoot {

  @Bean
  public RouterFunction<ServerResponse> routerFunction(OpenApiProvider openApiProvider) {
    return RouterFunctions
      .route()
      .GET("/v3/api-doc", request -> ServerResponse.ok().body(JSONUtil.toJsonStr(openApiProvider.getOpenApi())))
      .build();
  }

  public static void main(String[] args) {
    SpringApplication.run(ApiBoot.class, args);
  }

  @PostMapping("/user/{uid}")
  @Operation(summary = "获取用户信息1")
  public List<Result<SysUser>> user(@RequestParam(required = false) @Parameter(description = "姓名") String name,
                              @PathVariable @Parameter(description = "用户id") String uid,
                              @RequestBody SysUser user) {
    return Collections.singletonList(Result.success(new SysUser()));
  }

  @PostMapping("/user1")
  @Operation(summary = "获取用户信息2")
  public Result<SysUser> user1(@RequestBody List<SysUser> user) {
    return Result.success(new SysUser());
  }

  @PostMapping("/user2")
  @Operation(summary = "获取用户信息3")
  public Result<List<SysUser>> user2(@RequestBody SysUser[] user) {
    Assert.isTrue(ArrayUtil.isNotEmpty(user), "用户不能为空");
    return Result.success();
  }

  @PostMapping("/file")
  @Operation(summary = "上传文件")
  public MyResult<Map<String,List<SysUser>>> file(@RequestPart("file") MultipartFile file) {
    return new MyResult<>();
  }

  @Data
  public static class SysUser{
    @Schema(description = "用户id")
    private Long uid;

    @Schema(description = "用户名称")
    private String username;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "头像")
    private String avatar;

    @Schema(description = "创建时间")
    private Date createTime;

    @Schema(description = "逻辑删除")
    private EnumDel delFlag;
  }

  public static class MyResult<T> extends Result<T> {
    public String getMessage() {
      return super.getMsg();
    }
  }

  @Getter
  public enum EnumDel{
    EL(1, "删除"),
    NOT_DEL(0, "正常");
    ;
    private final int code;
    private final String desc;

    EnumDel(int code, String desc) {
      this.code = code;
      this.desc = desc;
    }
  }
}
