package com.ob

import com.ob.user.User
import com.ws.wx.WXBizMsgCrypt
import grails.rest.RestfulController
import org.springframework.beans.factory.annotation.Value


class WxBizController extends RestfulController{

    WxBizController(){
        super(WxBiz)
    }
    @Value('${wx_biz_corp}')
    String wx_biz_corp
    @Value('${wx_biz_agentId}')
    String wx_biz_agentId
    @Value('${wx_biz_secret}')
    String wx_biz_secret
    String token = "jlc5OtLEU8uV1eydMnSr4"  //验证的token
    String encoding_aes_key = "ceOEli095X0f8oO5oZoeF2EwtdnVP5DYTmGmfP2jsjj"
    def wxBizService

    def echo(){
        WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(token, encoding_aes_key, wx_biz_corp);
        String msg_signature = params.msg_signature
        String timestamp = params.timestamp
        String nonce  = params.nonce
        String echostr = params.echostr
        println "  msg_signature: "+msg_signature+"\n  timestamp:"+timestamp+"\n  nonce:"+nonce+"\n  echostr:"+echostr
        println "  wx_biz_corp:"+wx_biz_corp
        String sEchoStr = wxBizService.varifyUrl(msg_signature,timestamp,nonce,echostr)
        render sEchoStr
    }

    def getAccToken (){
        String acc = wxBizService.getAccessToken()
        println acc
        render acc
    }

    def msgRecieve(){
        println params
        render "OK"
    }

    def sendMsg(){
        String msg = "你的快递已到，请携带工卡前往邮件中心领取。\n出发前可查看<a href=\'http://work.weixin.qq.com\'>邮件中心视频实况</a>，聪明避开排队。"
        User u = User.findByUsername("songtao")
        wxBizService.sendMsg2party("1",msg)
        render "OK"
    }

}
