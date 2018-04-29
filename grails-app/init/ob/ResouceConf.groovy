package ob

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer
import org.springframework.security.oauth2.provider.token.DefaultTokenServices
import org.springframework.security.oauth2.provider.token.TokenStore
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore
import org.springframework.web.client.RestTemplate

import java.util.concurrent.TimeUnit

/**
 * Created by songtao on 2018/3/27.
 */
@Configuration
class ResouceConf extends ResourceServerConfigurerAdapter {
    @Bean
    RestTemplate restTemplate(){
        return new RestTemplate()
    }

//    @Bean
//    public BeanNameAutoProxyCreator beanNameAutoProxyCreator() {
//        BeanNameAutoProxyCreator beanNameAutoProxyCreator = new BeanNameAutoProxyCreator();
//        beanNameAutoProxyCreator.setBeanNames("tokenServices"); // If i put "*Service" instead, then i get an exception.
//        beanNameAutoProxyCreator.setProxyTargetClass(true)
//        return beanNameAutoProxyCreator;
//    }

    @Autowired
    RedisConnectionFactory redisConnectionFactory

    @Bean
    public TokenStore tokenStore() {
        return new RedisTokenStore(redisConnectionFactory)
    }

    @Bean
    @Primary  //这里必须定义primary  让resource在使用的时候选择这个 不然会有多个默认的实现 会报错.
    public DefaultTokenServices tokenServices() {
        final DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore());
        defaultTokenServices.setSupportRefreshToken(true);
        defaultTokenServices.setAccessTokenValiditySeconds((int) TimeUnit.DAYS.toSeconds(30)); // 30天
        return defaultTokenServices;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/api/dw/**").permitAll()
                .antMatchers("/api/user/**").hasRole("USER")
//                .antMatchers("/api/dw/**").hasRole("AMC")
                .antMatchers("/api/admin/**").hasRole("ADMIN")
                .antMatchers("/all/**").permitAll()
//            http.authorizeRequests().antMatchers("/api/dw/**").hasRole("AMC_EDITOR");
//            http.authorizeRequests().antMatchers("/api/dw/**").authenticated();
        //.antMatchers(HttpMethod.POST, "/foo").hasAuthority("FOO_WRITE");
        //you can implement it like this, but I show method invocation security on write
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        System.out.println("==========================Configuring ResourceServerSecurityConfigurer ");
        resources.resourceId("dw").tokenStore(tokenStore());
    }
}
