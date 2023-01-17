package top.mrys.example.domain;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Setter;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * 用户密码登录(SysLoginPassword)表实体类
 *
 * @author mrys
 */
@Setter
@Getter
@ToString
@Accessors(chain = true)
@TableName("sys_login_password")
public class SysLoginPassword extends Model<SysLoginPassword> {

  @Schema(description = "用户id")
  @TableId(type = IdType.AUTO)
  private Long uid;

  @Schema(description = "用户名称")
  private String username;

  @Schema(description = "密码")
  private String password;

  @Schema(description = "创建时间")
  private Date createTime;


}

