package com.ob

import com.ob.user.User
import grails.rest.RestfulController


class WxBizController extends RestfulController{

    WxBizController(){
        super(WxBiz)
    }
    static responseFormats = ['json','xml']
    def wxBizService

    def echo(){
        String msg_signature = params.msg_signature
        String timestamp = params.timestamp
        String nonce  = params.nonce
        String echostr = params.echostr
        println "  msg_signature: "+msg_signature+"\n  timestamp:"+timestamp+"\n  nonce:"+nonce+"\n  echostr:"+echostr
        String sEchoStr = wxBizService.varifyUrl(msg_signature,timestamp,nonce,echostr)
        render sEchoStr
    }

    def getAccToken (){
        String acc = wxBizService.getAccessToken()
        println acc
        render acc
    }

    def sendMsg(){
        String msg = "你的快递已到，请携带工卡前往邮件中心领取。\n出发前可查看<a href=\'http://work.weixin.qq.com\'>邮件中心视频实况</a>，聪明避开排队。"
        User u = User.findByUsername("songtao")
        wxBizService.sendMsg2party("1",msg)
        render "OK"
    }


    def msgRecieve(){
        String msg_signature = params.msg_signature
        String timestamp = params.timestamp
        String nonce = params.nonce
        BufferedReader bufferReader = request.getReader();//获取头部参数信息
        StringBuffer buffer = new StringBuffer();
        String line = " ";
        while ((line = bufferReader.readLine()) != null) {
            buffer.append(line);
        }
        String postData = buffer.toString();
        wxBizService.reciveMsg(msg_signature,timestamp,nonce,postData)
        render "OK"
    }

    def wxQyLogin(){
        def code = params.code
        println code
        String token = "1233";
        try {
            def user = wxBizService.getFromUserCode(code)
            token = wxBizService.genSysTokenFromUser(user)
        }catch (Exception e){
            response.status = 400
            render "error"
            return
        }
        render token
    }

}
