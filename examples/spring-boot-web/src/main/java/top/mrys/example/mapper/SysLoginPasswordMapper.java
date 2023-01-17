package top.mrys.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.mrys.example.domain.SysLoginPassword;

/**
 * 用户密码登录(SysLoginPassword)表数据库访问层
 *
 * @author mrys
 */
@Mapper
public interface SysLoginPasswordMapper extends BaseMapper<SysLoginPassword> {

}

