//package ob
//
//import com.ob.user.Role
//import com.ob.user.User
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.beans.factory.annotation.Qualifier
//import org.springframework.context.annotation.Bean
//import org.springframework.context.annotation.Configuration
//import org.springframework.data.redis.connection.RedisConnectionFactory
//import org.springframework.security.authentication.AuthenticationManager
//import org.springframework.security.authentication.AuthenticationProvider
//import org.springframework.security.authentication.ProviderManager
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
//import org.springframework.security.config.annotation.web.builders.HttpSecurity
//import org.springframework.security.config.annotation.web.builders.WebSecurity
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
//import org.springframework.security.core.Authentication
//import org.springframework.security.core.GrantedAuthority
//import org.springframework.security.core.authority.SimpleGrantedAuthority
//import org.springframework.security.core.userdetails.UserDetails
//import org.springframework.security.core.userdetails.UserDetailsService
//import org.springframework.security.core.userdetails.UsernameNotFoundException
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
//import org.springframework.security.crypto.password.PasswordEncoder
//import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer
//import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter
//import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer
//import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer
//import org.springframework.security.oauth2.provider.token.DefaultTokenServices
//import org.springframework.security.oauth2.provider.token.TokenStore
//import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore
//import org.springframework.stereotype.Component
//import org.springframework.stereotype.Service
//
//import javax.naming.AuthenticationException
//import java.util.concurrent.TimeUnit
//
///**
// * Created by songtao on 2018/3/23.
// */
//@Configuration
//class SuperAuth {
//
//    @Autowired
//    private AuthenticationProvider authenticationProvider;
//
//    @Bean
//    public AuthenticationManager authenticationManager() {
//        return new ProviderManager(Arrays.asList(authenticationProvider));
//    }
//
//    @Configuration
//    class AuthServer extends  AuthorizationServerConfigurerAdapter{
//        @Autowired
//        public AuthManager authenticationManager;
//        @Autowired
//        RedisConnectionFactory redisConnectionFactory
//        @Bean
//        public TokenStore tokenStore() {
////            return new JdbcTokenStore(datastore)
//            return new RedisTokenStore(redisConnectionFactory)
//        }
//
//        @Override
//        public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
//            endpoints.authenticationManager(authenticationManager);
//            endpoints.tokenStore(tokenStore())
//
//            // 配置TokenServices参数
//            DefaultTokenServices tokenServices = new DefaultTokenServices();
//            tokenServices.setTokenStore(endpoints.getTokenStore());
//            tokenServices.setSupportRefreshToken(false);
//            tokenServices.setClientDetailsService(endpoints.getClientDetailsService());
//            tokenServices.setTokenEnhancer(endpoints.getTokenEnhancer());
//            tokenServices.setAccessTokenValiditySeconds((int) TimeUnit.DAYS.toSeconds(30)); // 30天
//            endpoints.tokenServices(tokenServices);
//        }
//
//        @Override
//        public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
//            //        oauthServer.checkTokenAccess("isAuthenticated()");
//            oauthServer.checkTokenAccess("permitAll()");
//            oauthServer.allowFormAuthenticationForClients();
//        }
//
//        @Override
//        public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
//            clients.inMemory()
//                    .withClient("dw")
//                    .secret("dwsecret")
//                    .authorizedGrantTypes("authorization_code","password")
//                    .scopes("app");
//        }
//    }
//
//    @Configuration
//    class WebSecure extends WebSecurityConfigurerAdapter{
//        @Autowired
//        @Qualifier("userDetailsService")
//        UserDetailsService userDetailsService
//
//        @Bean
//        @Override
//        public AuthenticationManager authenticationManagerBean() throws Exception {
//            AuthenticationManager manager = super.authenticationManagerBean();
//            return manager;
//        }
//
//        @Override
//        protected void configure(HttpSecurity http) throws Exception {
//            http.authorizeRequests().antMatchers("/oauth/**").permitAll()
//                    .antMatchers("/login").permitAll()
//                    .antMatchers("/all/**").permitAll()
//                    .antMatchers("/api/user/**").hasRole("USER")
//                    .antMatchers("/api/dw/**").hasRole("AMC")
//                    .antMatchers("/api/admin/**").hasRole("ADMIN").and().csrf().disable()
////                    .and().authorizeRequests().antMatchers("/login").permitAll()
////                    .and().csrf().disable()
//        }
//
//        @Override
//        public void configure(WebSecurity web) throws Exception {
//            web.ignoring().antMatchers("/favor.ico");
//        }
//
//        @Bean
//        public PasswordEncoder passwordEncoder() {
//            return new BCryptPasswordEncoder();
//        }
//
//        @Autowired
//        AuthenticationProvider authenticationProvider
//
////        @Bean
////        public AuthenticationProvider authenticationProvider(){
////            def provider = new AuthProvider()
////            println "provider is .: " + provider
////            return  provider
////        }
//
//        @Override
//        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
////        auth.inMemoryAuthentication()
////                .withUser("reader")
////                .password("reader")
////                .authorities("FOO_READ")
////                .and()
////                .withUser("writer")
////                .password("writer")
////                .authorities("FOO_READ", "FOO_WRITE");
////
////        UserDetails userDetails = userDetailsService().loadUserByUsername("reader");
////        System.out.println(userDetails.getPassword());
//            println "===============aut =============="
//            auth.userDetailsService(userDetailsService)
////            auth.authenticationProvider(authenticationProvider())
//            auth.authenticationProvider(authenticationProvider)
//        }
//    }
//
//    @Component
//    class AuthProvider implements AuthenticationProvider{
//        @Autowired
//        @Qualifier("userDetailsService")
//        UserDetailsService userDetailsService
//        @Bean
//        public PasswordEncoder passwordEncoder() {
//            return new BCryptPasswordEncoder();
//        }
//        @Override
//        Authentication authenticate(Authentication authentication) throws AuthenticationException {
//            println "  "+authentication.getPrincipal()+"   "+authentication.getCredentials()+"        ===================================="
//            def user = userDetailsService.loadUserByUsername(authentication.getPrincipal())
//            if(!user){
//                throw new AuthenticationException("no user found")
//            }
//            def flag = passwordEncoder().matches(authentication.getCredentials(),user.password)
//            if(!flag){
//                throw new AuthenticationException("password error")
//            }
//            return new UsernamePasswordAuthenticationToken(authentication.getPrincipal(), authentication.getCredentials(),
//                    user.authorities);
//        }
//
//        @Override
//        boolean supports(Class<?> authentication) {
//            return true
//        }
//    }
//
//    @Component
//    class AuthManager implements AuthenticationManager{
//        @Autowired
//        AuthProvider authProvider
//
//        @Override
//        Authentication authenticate(Authentication authentication) throws org.springframework.security.core.AuthenticationException {
//            println "----------------使用provider的auth-------------------------------"
//            return authProvider.authenticate(authentication)
//        }
//    }
//
//    @Component
//    @Service("userDetailsService")
//    class CustomUserDetailsService implements UserDetailsService{
//
//        @Override
//        UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//            User user = User.findByUsername(username)
//            if(!user){
//                throw new UsernameNotFoundException("user not found",username)
//            }
//            System.out.println("User : "+user);
//            if(user==null){
//                System.out.println("User not found");
//                throw new UsernameNotFoundException("Username not found");
//            }
//            return new org.springframework.security.core.userdetails.User(user.username, user.password,
//                    user.enabled, !user.accountExpired,!user.passwordExpired,!user.accountLocked, getGrantedAuthorities(user));
//
//        }
//
//        private List<GrantedAuthority> getGrantedAuthorities(User user){
//            List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
//
//            for(Role aut in user.authorities){
//                authorities.add(new SimpleGrantedAuthority(aut.authority))
//            }
//            System.out.print("authorities :"+authorities);
//            return authorities;
//        }
//
//
//    }
//
//
//}
