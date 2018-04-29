package com.ws.oauth

import org.grails.web.json.JSONObject


/**
 * Created by songtao on 2018/3/29.
 */
interface OauthService {
    public  String getAccessToken(String code);
    public  String getOpenId(String accessToken);
    public  String refreshToken(String code);
    public  String getAuthorizationUrl() throws UnsupportedEncodingException;
    public  JSONObject getUserInfo(String accessToken, String openId);
}