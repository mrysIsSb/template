package top.mrys.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.mrys.example.domain.TenantRole;

/**
 * 租户角色表(TenantRole)表数据库访问层
 *
 * @author mrys
 */
@Mapper
public interface TenantRoleMapper extends BaseMapper<TenantRole> {

}

