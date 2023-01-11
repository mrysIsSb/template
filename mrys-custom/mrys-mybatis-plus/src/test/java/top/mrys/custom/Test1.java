package top.mrys.custom;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import top.mrys.core.PageParam;
import top.mrys.custom.emtitys.CustomerSku;
import top.mrys.custom.emtitys.EnumDelFlag;
import top.mrys.custom.mapper.CustomerSkuMapper;

import java.util.List;

/**
 * @author mrys
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {Test1.class})
@ComponentScan("top.mrys.custom")
@EnableAutoConfiguration
@SpringBootTest
@Slf4j
public class Test1 {

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Autowired
    private CustomerSkuMapper customerSkuMapper;

    @Test
    public void test(){
        System.out.println(sqlSessionFactory);
        SqlSessionFactory factory = SqlHelper.FACTORY;
        Configuration configuration = factory.getConfiguration();
        GlobalConfig globalConfig = GlobalConfigUtils.getGlobalConfig(configuration);
//        List<Map<String, Object>> maps = SqlRunner.db().selectList("select * from customer_sku limit 1");
        CustomerSku customerSku = new CustomerSku()
                .setUuid("00208ea3e078454fa770835509029f4d")
                .selectById();
        log.info("customerSku:{}", customerSku);
        TableInfo customer_sku = TableInfoHelper.getTableInfo("customer_sku");
      QueryWrapper<CustomerSku> queryWrapper = new QueryWrapper<>();
      List<CustomerSku> customerSkus = customerSkuMapper.selectPage(PageParam.from(1L,10L).convert(IPage.class),queryWrapper.lambda()
        .eq(CustomerSku::getUuid, "00208ea3e078454fa770835509029f4d")
        .eq(CustomerSku::getDelFlag, EnumDelFlag.NOT_DEL)).getRecords();
    }

}
