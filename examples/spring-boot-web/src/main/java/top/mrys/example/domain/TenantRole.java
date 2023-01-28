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
import top.mrys.custom.EnumActionType;
import top.mrys.custom.TenantId;
import top.mrys.example.annotations.UidFill;

import java.util.Date;

/**
 * 租户角色表(TenantRole)表实体类
 *
 * @author mrys
 */
@Setter
@Getter
@ToString
@Accessors(chain = true)
@TableName("tenant_role")
public class TenantRole extends Model<TenantRole> {

  @Schema(description = "角色id")
  @TableId(type = IdType.AUTO)
  private Long roleId;

  @Schema(description = "角色code")
  private String roleCode;

  @Schema(description = "角色名")
  private String roleName;

  @Schema(description = "创建时间")
  private Date createTime;

  @Schema(description = "创建人")
  @UidFill(types = {EnumActionType.INSERT})
  private Long createUid;

  @Schema(description = "租户id")
  @TenantId
  private Long tenantId;


}

