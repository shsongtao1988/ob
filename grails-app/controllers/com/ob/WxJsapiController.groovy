package com.ob

import grails.converters.JSON
import grails.rest.RestfulController

class WxJsapiController extends RestfulController{
    WxJsapiController(){
        super(WxJsapi)
    }
    static responseFormats = ['json','xml']

    def wxJsapiService

    def getTicket(){
        String ticket = wxJsapiService.getTicket()
        render ticket
    }

    def getWxConfig(){
        String url = params.url
        String timestamp = ((new Date().getTime()/1000) as Long).toString()
        String nonce = UUID.randomUUID().toString()
        String ticket = wxJsapiService.getTicket()
        String string1 = "jsapi_ticket="+ticket+"&noncestr="+nonce+"&timestamp="+timestamp+"&url="+url
        String signature = string1.encodeAsSHA1().toString()
        String corpId = wxJsapiService.wxBizService.wx_biz_corp
        String json = "{ url:\"${url}\",ticket:\"${ticket}\" ,beta: true,debug: true,signature:${signature},appId:${corpId},timestamp:${timestamp},nonceStr:${nonce}}"
        println string1
        respond JSON.parse(json)
    }

    def recievePicServerIds(){
        def ids = params.serverIds
        println ids
        render "OK"
    }

}
