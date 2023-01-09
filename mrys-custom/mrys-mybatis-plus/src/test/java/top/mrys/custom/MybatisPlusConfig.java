package top.mrys.custom;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.autoconfigure.SqlSessionFactoryBeanCustomizer;
import com.baomidou.mybatisplus.core.handlers.PostInitTableInfoHandler;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author mrys
 */
@MapperScan("top.mrys.custom.mapper")
@Configuration(proxyBeanMethods = false)
@Slf4j
public class MybatisPlusConfig {


    @Bean
    public PostInitTableInfoHandler postInitTableInfoHandler() {
        return new PostInitTableInfoHandler() {
            @Override
            public void postFieldInfo(TableFieldInfo fieldInfo, org.apache.ibatis.session.Configuration configuration) {
                log.info("fieldInfo:{}", fieldInfo);
            }

            @Override
            public void postTableInfo(TableInfo tableInfo, org.apache.ibatis.session.Configuration configuration) {
                log.info("tableInfo:{}", tableInfo);
            }
        };
    }


    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        interceptor.addInnerInterceptor(new TenantLineInnerInterceptor(new TenantLineHandler() {
            @Override
            public Expression getTenantId() {
                return new StringValue("b94a46479df74ae3a9e61aa909a1fc37");
            }

            @Override
            public String getTenantIdColumn() {
                return "customerUuid";
            }
        }));
        return interceptor;
    }


    @Bean
    public SqlSessionFactoryBeanCustomizer sqlSessionFactoryBeanCustomizer() {
        return factoryBean -> {
            System.out.println("自定义mybatis plus配置");


/*            MybatisConfiguration configuration = factoryBean.getConfiguration();
            SqlSourceBuilder sqlSourceBuilder = new SqlSourceBuilder(configuration);
            sqlSourceBuilder.parse()
            MappedStatement.Builder statementBuilder = new MappedStatement.Builder(configuration, "hello", sqlSource, SqlCommandType.SELECT)
                    .resource(resource)
                    .fetchSize(fetchSize)
                    .timeout(timeout)
                    .statementType(statementType)
                    .keyGenerator(keyGenerator)
                    .keyProperty(keyProperty)
                    .keyColumn(keyColumn)
                    .databaseId(databaseId)
                    .lang(lang)
                    .resultOrdered(resultOrdered)
                    .resultSets(resultSets)
                    .resultMaps(getStatementResultMaps(resultMap, resultType, id))
                    .resultSetType(resultSetType)
                    .flushCacheRequired(valueOrDefault(flushCache, !isSelect))
                    .useCache(valueOrDefault(useCache, isSelect))
                    .cache(currentCache);
            configuration.addMappedStatement(statementBuilder.build());*/
        };
    }
}
