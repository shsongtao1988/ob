package com.ob

import com.ob.user.Depart
import com.ob.user.User
import com.ws.wx.WXBizMsgCrypt
import grails.plugins.rest.client.RestBuilder
import grails.transaction.Transactional
import org.grails.web.json.JSONObject
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

    private String access_token = null
    Date expire_date = new Date()

    /**
     * 每一个半小时获取一次accessToken 或者 出现42001的错误 也要重新获取accessToken
     * @return
     */
    String getAccessToken(){//单例模式 获取accessToken  加时间判断的  如果微信方面导致失效42001错误 就调用getToken
        if(access_token != null && expire_date.getTime()>(new Date().getTime()+30*60*1000)){//提前半小时失效
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
        sendMsg(user.username,null,msg,0)
    }
    /**
     * 对一个部门发送消息
     * @param party
     * @param msg
     * @return
     */
    def sendMsg2party(String party,String msg){
        sendMsg(null,party,msg,0)
    }

    /**
     * 对全体成员发送消息
     * @param msg
     */
    def sendMsg2all(String msg){
        sendMsg("@all",null,msg,0)
    }

    def sendMsg(String toUser,String toParty,String msg,int tryCount){
        if(tryCount>2){
            throw new RuntimeException("can not send message to user ..") //超过3次了
        }
        JSONObject message = new JSONObject()
        if(toUser){
            message["touser"] = toUser
        }
        if(toParty){
            message["toparty"] = toParty
        }
        message["agentid"] = "${wx_biz_agentId}"
        message["msgtype"] = "text"
        message["text"] = new JSONObject()
        message["text"]["content"] = "$msg"
        message["safe"] = 0
        String url ="https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token="+getAccessToken()
        def resp = null
        try{
            def rest = new RestBuilder()
            resp = rest.post(url){
                header "content-type","text/html;charset=utf-8"
                json message.toString()
            }
        }catch (Exception e){
            println e.message
        }
        def body = resp.json
        if(body.errcode == 42001){//42001
            getToken()//已有的token失效了 再来一次
            sendMsg(toUser,toParty,msg,tryCount+1)
        }
        println body
    }
    /**
     * 处理接收到的信息
     * @param msg
     */
    def reciveMsg(String msg_signature,String timestamp,String nonce,String msg){
        def xmlText = getWxcpt().DecryptMsg(msg_signature,timestamp,nonce,msg)
        def xml = new XmlParser().parseText(xmlText)
        def crtTime = xml["CreateTime"].text()
        def msgType = xml["MsgType"].text()
        if(msgType == "event"){//event

        }else{//其他类型的消息 如 text image voice video location link 等在这里处理

        }
    }

    def eventMsg(String xmlText){
        def xml = new XmlParser().parseText(xmlText)
        String msgType = xml["MsgType"].text()
        def username = xml["FromUserName"].text()
        switch (msgType){
            case "subscribe":   //关注
                def user = User.findByUsername(username)
                user.enabled = true
                user.save(flush:true)
                break;
            case "unsubscribe":  //取消关注
                def user = User.findByUsername(username)
                user.enabled = false
                user.save(flush:true)
                break;
            case "enter_agent":     //进入应用
                println username+" 进入应用了"
                break;
            case "LOCATION":        //发送地理位置
                String Latitude = xml["Latitude"].text()
                String Longitude = xml["Longitude"].text()
                println username+" 的地理坐标为: "+Latitude+", "+Longitude
                break;
            case "batch_job_result":    //异步任务完成事件推送
                println "完成异步事件推送 不知道干啥的 ."
                break;
            case "change_contact":      //通讯录变更事件
                String changeType = xml["ChangeType"].text()
                switch (changeType){
                    case "create_user":
                        createUser(xml)
                        break;
                    case "update_user":
                        updateUser(xml)
                        break
                    case "delete_user":
                        deleteUser(xml)
                        break
                    case "create_party":
                        createDepart(xml)
                        break
                    case "update_party":
                        updateDepart(xml)
                        break
                    case "delete_party":
                        deleteDepart(xml)
                        break
                    default:
                        println "其他的如建立标签之类的不做解析"
                        break
                }
                break;
            case "click":               //点击菜单拉取消息的事件推送
                break;
            case "view":                //点击菜单跳转链接的事件推送
                break;
            case "scancode_push":       //扫码推事件的事件推送
                break;
            case "scancode_waitmsg":    //扫码推事件且弹出“消息接收中”提示框的事件推送
                break;
            case "pic_sysphoto":        //弹出系统拍照发图的事件推送
                break;
            case "pic_photo_or_album":  //弹出拍照或者相册发图的事件推送
                break;
            case "pic_weixin":          //弹出微信相册发图器的事件推送
                break;
            case "location_select":     //弹出地理位置选择器的事件推送
                break;
            default:
                break;
        }
    }

    def createUser(Node xml){
        String username = xml["UserID"].text()      //UserID	成员UserID
        String name = xml["Name"].text()            //Name	成员名称
        String depart = xml["Department"].text()    //Department	成员部门列表
        String mobile = xml["Mobile"].text()        //Mobile	手机号码
        String position = xml["Position"].text()    //Position	职位信息。长度为0~64个字节
        String gender = xml["Gender"].text()        //Gender	性别，1表示男性，2表示女性
        String email = xml["Email"].text()          //Email	邮箱
        String status = xml["Status"].text()        //Status	激活状态：1=已激活 2=已禁用
        String Avatar = xml["Avatar"].text()        //Avatar	头像url。注：如果要获取小图将url最后的”/0”改成”/100”即可。
        String englishName = xml["EnglishName"].text()//EnglishName	英文名
        String isLeader = xml["IsLeader"].text()    //IsLeader	上级字段，标识是否为上级。0表示普通成员，1表示上级
        String telephone = xml["Telephone"].text()  //Telephone	座机
        User user = new User(username:username, name:name, nickname: name,mobile:mobile)
        user.save(flush:true)
        def arr = depart.split(",")
        for(String item in arr){
            def departId  = item as Long
            def itemDepart = Depart.findById(departId)
            //这里将depart和user关联起来
        }
        //将头像传到我们系统中
        //userProfile
    }
    def updateUser(Node xml){
        //先不写
    }
    def deleteUser(Node xml){
        //回头写
    }
    def createDepart(Node xml){
        //看情况写
    }
    def updateDepart(Node xml){
        //看心情写
    }
    def deleteDepart(Node xml){
        //想写就写
    }

    def getFromUserCode(String code){
        String url = "https://qyapi.weixin.qq.com/cgi-bin/user/getuserinfo?access_token="+getAccessToken()+"&code="+code
        def rest = new RestBuilder()
        def resp = rest.get(url)
        def json = resp.json
        if(json.errmsg != "ok"){
            log.error(json.errmsg)
            throw new RuntimeException(json.errmsg)
        }
        if(json.UserId){
            def user = User.findByUsername(json.UserId)
            return user
        }
        if(json.OpenId){//要不要在第三方登录中显示这个呢??

        }
        return  null
    }
    def genSysTokenFromUser(User user){
        return "ThisIsMySystemTokenAndEveryOneCanUse"
    }

}
