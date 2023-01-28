package top.mrys.example.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotatedElementUtils;
import top.mrys.custom.TenantId;
import top.mrys.custom.core.AuthTool;
import top.mrys.custom.core.UserInfo;

import java.util.Optional;

/**
 * @author mrys
 */
@Configuration(proxyBeanMethods = false)
@MapperScan("top.mrys.example.mapper")
public class MybatisConfig {
  @Bean
  public MybatisPlusInterceptor mybatisPlusInterceptor() {
    MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
    interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        interceptor.addInnerInterceptor(new TenantLineInnerInterceptor(new TenantLineHandler() {
          private InheritableThreadLocal<String> tenantId = new InheritableThreadLocal<>();
          @Override
          public Expression getTenantId() {
            return AuthTool.getUserInfoOptional()
              .map(UserInfo::getTenantId)
              .map(StringValue::new)
              .orElse(null);
          }

          @Override
          public String getTenantIdColumn() {
            return tenantId.get();
          }

          @Override
          public boolean ignoreTable(String tableName) {
            tenantId.remove();
            if (AuthTool.isSuperAdmin()) {
              //超级管理员不过滤
              return true;
            }
            TableInfo tableInfo = TableInfoHelper.getTableInfo(tableName);
            Optional<TenantId> tenantId = tableInfo.getFieldList().stream()
              .filter(fieldInfo -> AnnotatedElementUtils.hasAnnotation(fieldInfo.getField(), TenantId.class))
              .findFirst()
              .map(fieldInfo -> AnnotatedElementUtils.findMergedAnnotation(fieldInfo.getField(), TenantId.class));
            tenantId.ifPresent(tid -> this.tenantId.set(tid.value()));
            return tenantId.isEmpty();
          }
        }));
    return interceptor;
  }
  
}
