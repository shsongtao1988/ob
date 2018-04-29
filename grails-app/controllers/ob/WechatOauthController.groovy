package ob

import com.ob.user.User
import grails.converters.JSON
import org.grails.web.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.common.OAuth2AccessToken
import org.springframework.security.oauth2.provider.OAuth2Authentication
import org.springframework.security.oauth2.provider.token.DefaultTokenServices

import javax.servlet.http.Cookie

class WechatOauthController {

    def wechatOauthService

    public String wxLoginPage() throws Exception {
        String uri = wechatOauthService.getAuthorizationUrl();
        render uri;
    }

    @Autowired
    DefaultTokenServices tokenService

    def wechat() throws Exception {
        String code = params.code
        String result = wechatOauthService.getAccessToken(code);
        JSONObject jsonObject = JSON.parse(result);

        String access_token = jsonObject.getString("access_token");
        String openId = jsonObject.getString("openId");
//        String refresh_token = jsonObject.getString("refresh_token");

        // 保存 access_token 到 cookie，两小时过期
        Cookie accessTokencookie = new Cookie("accessToken", access_token);
        accessTokencookie.setMaxAge(60 *2);
        response.addCookie(accessTokencookie);

        Cookie openIdCookie = new Cookie("openId", openId);
        openIdCookie.setMaxAge(60 *2);
        response.addCookie(openIdCookie);

        def userInfo = wechatOauthService.getUserInfo(access_token,openId)
        println userInfo.toString()

        User user = User.findById(1)
        String username = user.username
        println username
        Authentication authentication = UsernamePasswordAuthenticationToken(username, "", user.authorities)

        OAuth2Authentication auth = tokenService.getAccessToken(authentication);
        println auth
        OAuth2AccessToken token = tokenService.getAccessToken(auth)
        println token

        response.sendRedirect("/")

    }
}
