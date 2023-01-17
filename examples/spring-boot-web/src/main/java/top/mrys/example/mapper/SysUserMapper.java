package top.mrys.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.mrys.example.domain.SysUser;

/**
 * 系统用户表(SysUser)表数据库访问层
 *
 * @author
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

}

