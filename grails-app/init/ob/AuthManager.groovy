package ob

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component

/**
 * Created by songtao on 2018/3/26.
 */
@Component
class AuthManager implements AuthenticationManager{
    @Autowired
    AuthProvider authProvider

    @Override
    Authentication authenticate(Authentication authentication) throws org.springframework.security.core.AuthenticationException {
        println "----------------使用provider的auth-------------------------------"
        return authProvider.authenticate(authentication)
    }
}
