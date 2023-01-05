package top.mrys.custom.config;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.mrys.custom.filters.authenticate.PropertiesTokenAuthenticateProvider;
import top.mrys.custom.login.functions.LocalLoginFunction;

/**
 * @author mrys
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "security.local", name = "enable", havingValue = "true")
@ConditionalOnBean(AutoConfigurationSecurity.class)
@AutoConfigureAfter(AutoConfigurationSecurity.class)
public class AutoConfigurationLocal {
    @Bean
    public PropertiesTokenAuthenticateProvider propertiesTokenAuthenticateProvider(SecurityProperties securityProperties) {
        return new PropertiesTokenAuthenticateProvider(securityProperties.getLocal().getUsers());
    }

    @Bean
    public LocalLoginFunction localLoginFunction(SecurityProperties securityProperties) {
        return new LocalLoginFunction(securityProperties.getLocal().getUsers());
    }
}
