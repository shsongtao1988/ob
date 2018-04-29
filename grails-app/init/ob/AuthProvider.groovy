package ob

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.stereotype.Component

import javax.naming.AuthenticationException

/**
 * Created by songtao on 2018/3/26.
 */
@Component
class AuthProvider implements AuthenticationProvider{
    @Autowired
    @Qualifier("userDetailsService")
    UserDetailsService userDetailsService
    @Autowired
    PasswordEncoder passwordEncoder

    @Override
    Authentication authenticate(Authentication authentication) throws AuthenticationException {
        println "  "+authentication.getPrincipal()+"   "+authentication.getCredentials()+"        ===================================="
        def user = userDetailsService.loadUserByUsername(authentication.getPrincipal())
        if(!user){
            throw new AuthenticationException("no user found")
        }
//        def flag = passwordEncoder().matches(authentication.getCredentials(),user.password)

        def flag1 = (passwordEncoder.matches(authentication.getCredentials(),user.password))
        def flag2 = false
        if(authentication instanceof OAuth2Authentication){ //由微信等第三方登录时,由服务器端产生的authentication是这种形式的
            def auth = authentication as OAuth2Authentication
            String pass = auth.userAuthentication.getCredentials()
            flag2 = pass.equals(user.password)
        }
        def flag = (flag1 || flag2)
        if(!flag){
            throw new AuthenticationException("password error")
        }
//        return new UsernamePasswordAuthenticationToken(authentication.getPrincipal(), authentication.getCredentials(), user.authorities);
        return new UsernamePasswordAuthenticationToken(authentication.getPrincipal(), user.password, user.authorities);
    }

    @Override
    boolean supports(Class<?> authentication) {
        return true
    }
}
