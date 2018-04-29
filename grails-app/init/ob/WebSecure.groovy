package ob

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.oauth2.provider.token.TokenStore

import java.util.concurrent.TimeUnit

/**
 * Created by songtao on 2018/3/26.
 */
@Configuration
class WebSecure extends WebSecurityConfigurerAdapter{
    @Autowired
    @Qualifier("userDetailsService")
    UserDetailsService userDetailsService

    @Autowired
    TokenStore tokenStore

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        AuthenticationManager manager = super.authenticationManagerBean();
        return manager;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/oauth/**").permitAll()
                .antMatchers("/login").permitAll().and().csrf().disable()
//                    .and().authorizeRequests().antMatchers("/login").permitAll()
//                    .and().csrf().disable()
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/favor.ico");
    }

    @Autowired
    AuthenticationProvider authenticationProvider
//        @Bean
//        public AuthenticationProvider authenticationProvider(){
//            def provider = new AuthProvider()
//            println "provider is .: " + provider
//            return  provider
//        }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.inMemoryAuthentication()
//                .withUser("reader")
//                .password("reader")
//                .authorities("FOO_READ")
//                .and()
//                .withUser("writer")
//                .password("writer")
//                .authorities("FOO_READ", "FOO_WRITE");
//
//        UserDetails userDetails = userDetailsService().loadUserByUsername("reader");
//        System.out.println(userDetails.getPassword());
        println "===============aut =============="
        auth.userDetailsService(userDetailsService)
//            auth.authenticationProvider(authenticationProvider())
        auth.authenticationProvider(authenticationProvider)
    }
}