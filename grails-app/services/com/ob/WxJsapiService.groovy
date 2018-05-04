package com.ob

import grails.plugins.rest.client.RestBuilder
import grails.transaction.Transactional

@Transactional
class WxJsapiService {
    def serviceMethod() {}
    def wxBizService
    private String js_ticket = null
    Date expire_date = new Date()

    String getTicket(){
        if(js_ticket !=null && expire_date.getTime()<(new Date().getTime()+30*60*1000)){//提前版小时失效
            return js_ticket
        }
        return fromWxGetTicket()
    }
    private String fromWxGetTicket(){
        String token = wxBizService.getAccessToken()
        String url = "https://qyapi.weixin.qq.com/cgi-bin/get_jsapi_ticket?access_token="+token
        println "start to get access token "+url+"  "+ (new Date().getTime())
        try {
            def rest = new RestBuilder()
            def resp = rest.post(url)
            def json = resp.json
            if(json."errmsg"=="ok"){
                expire_date = new Date((new Date().getTime()+json."expires_in" *1000))  //设置在状态中
                js_ticket = json."ticket"
                return js_ticket
            }
        }catch (Exception e){
            throw new RuntimeException("get ticket error ")
        }
    }


}
