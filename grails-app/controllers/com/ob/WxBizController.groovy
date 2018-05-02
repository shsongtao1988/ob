package com.ob

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

    def echo(){
        WXBizMsgCrypt wxcpt = new WXBizMsgCrypt(token, encoding_aes_key, wx_biz_corp);
        String msg_signature = params.msg_signature
        String timestamp = params.timestamp
        String nonce  = params.nonce
        String echostr = params.echostr
        println "  msg_signature: "+msg_signature+"\n  timestamp:"+timestamp+"\n  nonce:"+nonce+"\n  echostr:"+echostr
        println "  wx_biz_corp:"+wx_biz_corp
        String sEchoStr = ""
        try {
            sEchoStr = wxcpt.VerifyURL(msg_signature, timestamp, nonce, echostr);
            System.out.println("verifyurl echostr: " + sEchoStr);
        }catch (Exception e){
            println e.message
        }
        render sEchoStr
    }

}
