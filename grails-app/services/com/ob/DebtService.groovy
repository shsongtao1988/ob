package com.ob

import com.ob.user.Role
import com.ob.user.User
import grails.transaction.Transactional
import ob.AuthManager
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.common.OAuth2AccessToken
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.security.oauth2.provider.OAuth2Request
import org.springframework.security.oauth2.provider.token.DefaultTokenServices
import org.springframework.security.oauth2.provider.token.TokenStore

import java.util.concurrent.TimeUnit

@Transactional
class DebtService {

    def serviceMethod() { }

    @Autowired
    TokenStore tokenStore
    @Autowired
    AuthManager authManager

    DefaultTokenServices singleService = null

    def getSigleTokenService(){
        if(singleService != null){
            return singleService
        }
        DefaultTokenServices tokenServices = new DefaultTokenServices()
        tokenServices.setTokenStore(tokenStore)
        tokenServices.setAuthenticationManager(authManager)
        tokenServices.setSupportRefreshToken(true);
        tokenServices.setAccessTokenValiditySeconds((int) TimeUnit.DAYS.toSeconds(30)); // 30天
        this.singleService = tokenServices
        return tokenServices
    }

    def genUserToken(User user){
        DefaultTokenServices tokenServices = getSigleTokenService()
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        for(Role aut in user.authorities){
            authorities.add(new SimpleGrantedAuthority(aut.authority))
        }
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.username,user.password,authorities)
        OAuth2Request oAuth2Request = new OAuth2Request(clientId: "dw")
        OAuth2Authentication authentication = new OAuth2Authentication(oAuth2Request,authenticationToken)
        OAuth2AccessToken token = tokenServices.createAccessToken(authentication);
        return token
    }
    def tstService
    def debtDelay(Debt debt){
        Map map = new HashMap()
        map.put("type","debt")
        map.put("object",debt)
        println new Date().toTimestamp()
        def delay = new Delay()
        delay.map = map
        delay.tstService = tstService
        new Thread(delay).start()
        println new Date().toTimestamp()
    }
}
