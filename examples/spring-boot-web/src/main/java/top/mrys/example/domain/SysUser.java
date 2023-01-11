package top.mrys.example.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * 系统用户表(SysUser)表实体类
 *
 * @author
 */
@Setter
@Getter
@ToString
@Accessors(chain = true)
@TableName("sys_user")
public class SysUser extends Model<SysUser> {

  @Schema(description = "用户id")
  @TableId(type = IdType.AUTO)
  private Long uid;

  @Schema(description = "用户名称")
  private String username;

  @Schema(description = "昵称")
  private String nickname;

  @Schema(description = "头像")
  private String avatar;

  @Schema(description = "创建时间")
  private Date createTime;


}

