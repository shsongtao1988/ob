package com.ob

import com.ob.user.User
import com.ws.wx.WXBizMsgCrypt
import grails.plugins.rest.client.RestBuilder
import grails.transaction.Transactional
import org.springframework.beans.factory.annotation.Value

@Transactional
class WxBizService {
    def serviceMethod() {}
    @Value('${wx_biz_corp}')
    String wx_biz_corp
    @Value('${wx_biz_agentId}')
    String wx_biz_agentId
    @Value('${wx_biz_secret}')
    String wx_biz_secret
    String token = "jlc5OtLEU8uV1eydMnSr4"  //验证的token
    String encoding_aes_key = "ceOEli095X0f8oO5oZoeF2EwtdnVP5DYTmGmfP2jsjj"

    WXBizMsgCrypt wxcpt = null;

    WXBizMsgCrypt getWxcpt (){//单例模式 获取解密类
        if(wxcpt==null){
            wxcpt = new WXBizMsgCrypt(token, encoding_aes_key, wx_biz_corp);
        }
        return wxcpt
    }
    /**
     * 验证url的信息
     * @param msg_signature
     * @param timestamp
     * @param nonce
     * @param echostr
     * @return
     */
    String varifyUrl(String msg_signature,String timestamp,String nonce,String echostr){
        def wxcpt = getWxcpt()
        String echoStr = ""
        try{
            echoStr = wxcpt.VerifyURL(msg_signature, timestamp, nonce, echostr);
        }catch (Exception e){
            println e.message
        }
        return echoStr
    }

    private String access_token = "cvr-s3CeWmSaB1OiIOv6xo58br8fiP3z3F-usahxmZfXqol6pCdsU3Zl8kk7Qs8bS38yMoNEHvZw1ppa-l26fCD098Og0vaXYHUASs7GWKRYNxMcIvnXxMQ9kqGknHw1Z-R1R4v8ob4zClhoRMVXlKGw1MPuN9x_5WzTNIM-A8MY6qwLYRnO9BC8R0ImaaECl3O2nQqdi1HHug3zVzs1Pw"
    Date expire_date = new Date()

    /**
     * 每一个半小时获取一次accessToken 或者 出现42001的错误 也要重新获取accessToken
     * @return
     */
    String getAccessToken(){//单例模式 获取accessToken  加时间判断的  如果微信方面导致失效42001错误 就调用getToken
        if(expire_date.getTime()<(new Date().getTime()+30*60*1000)){//提前版小时失效
            return access_token
        }
        return getToken()
    }
    private String getToken(){
        String url ="https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=${wx_biz_corp}&corpsecret=${wx_biz_secret}"
        println "start to get access token "+url+"  "+ (new Date().getTime())
        try {
            def rest = new RestBuilder()
            def resp = rest.post(url)
            def json = resp.json
            if(json."errmsg"=="ok"){
                expire_date = new Date((new Date().getTime()+json."expires_in" *1000))  //设置在状态中
                access_token = json."access_token"
                return json."access_token"
            }
        }catch (Exception e){
            throw new RuntimeException("get access token error ")
        }
    }

    /**
     * 发送消息给单独的用户
     * @param user
     * @param msg
     * @return
     */
    def sendMsgToUser(User user,String msg){
        sendMsgToUser(user,msg,0)
    }
    def sendMsgToUser(User user,String msg,int tryCount){
        if(tryCount>2){
            throw new RuntimeException("can not send message to user ..") //超过3次了
        }
        String url ="https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token="+getAccessToken()
        def resp = null
        try{
            def rest = new RestBuilder()
            resp = rest.post(url){
                header "content-type","text/html;charset=utf-8"
                json """
                    {
                    "touser":"${user.username}",
                    "msgtype":"text",
                    "agentid":"${wx_biz_agentId}",
                    "text":{
                        "content": "$msg"
                        },
                    "safe":0
                    }
                   """
                }
        }catch (Exception e){
            println e.message
        }
        def body = resp.json
        if(body.errcode == 42001){//42001
            getToken()//已有的token失效了 再来一次
            sendMsgToUser(user,msg,tryCount+1)
        }
    }

    def sendMsg2party(String party,String msg){
        sendMsg2party(party,msg,0)
    }
    def sendMsg2party(String party,String msg,int tryCount){
        if(tryCount>2){
            throw new RuntimeException("can not send message to user ..") //超过3次了
        }
        String url ="https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token="+getAccessToken()
        def resp = null
        try{
            def rest = new RestBuilder()
            resp = rest.post(url){
                header "content-type","text/html;charset=utf-8"
                json """
                    {
                    "toparty":"${party}",
                    "msgtype":"text",
                    "agentid":"${wx_biz_agentId}",
                    "text":{
                        "content": "$msg"
                        },
                    "safe":0
                    }
                   """
            }
        }catch (Exception e){
            println e.message
        }
        def body = resp.json
        if(body.errcode == 42001){//42001
            getToken()//已有的token失效了 再来一次
            sendMsg2party(party,msg,tryCount+1)
        }
        println body
    }
}
