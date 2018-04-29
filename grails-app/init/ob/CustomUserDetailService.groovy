package ob

import com.ob.user.Role
import com.ob.user.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service

/**
 * Created by songtao on 2018/3/26.
 */
@Component
@Service("userDetailsService")
class CustomUserDetailService implements UserDetailsService{

    @Override
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = User.findByUsername(username)
        if(!user){
            throw new UsernameNotFoundException("user not found",username)
        }
        System.out.println("User : "+user);
        if(user==null){
            System.out.println("User not found");
            throw new UsernameNotFoundException("Username not found");
        }
        return new org.springframework.security.core.userdetails.User(user.username, user.password,
                user.enabled, !user.accountExpired,!user.passwordExpired,!user.accountLocked, getGrantedAuthorities(user));

    }

    private List<GrantedAuthority> getGrantedAuthorities(User user){
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();

        for(Role aut in user.authorities){
            authorities.add(new SimpleGrantedAuthority(aut.authority))
        }
        System.out.print("authorities :"+authorities);
        return authorities;
    }
}
