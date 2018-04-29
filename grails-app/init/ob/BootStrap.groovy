package ob

import com.ob.user.Requestmap
import com.ob.user.Role
import com.ob.user.User
import com.ob.user.UserRole

class BootStrap {

    def init = { servletContext ->
        initUser()
        initSecu()
    }
    def destroy = {
    }
    def initUser(){
        def u = User.findById(1)
        if(u){
            println "has user . not init ."
            return
        }
        def user1 = new User(username: "songtao",password: "password1",nickname: "songtao_nick").save(flush:true)
        def user2 = new User(username: "yhan",password: "password1",nickname: "yhan_nick").save(flush:true)
        def user3 = new User(username: "zmj",password: "password1",nickname: "zmj_nick").save(flush:true)
        def role1 = new Role("ROLE_USER").save(flush:true)
        def role2 = new Role("ROLE_AMC").save(flush:true)
        def role3 = new Role("ROLE_ADMIN").save(flush:true)
        UserRole.create(user1,role1,true)
        UserRole.create(user1,role2,true)
        UserRole.create(user1,role3,true)
        UserRole.create(user2,role2,true)
        UserRole.create(user3,role3,true)
    }
    def initSecu(){
        def securedUrls = [
                [url: '/api/dw/**', configAttribute: 'ROLE_USER'],
                [url: '/api/spec', configAttribute: 'ROLE_ADMIN, ROLE_AMC'],
                [url: '/api/user/**', configAttribute: 'isFullyAuthenticated()'],
                [url: '/all/**', configAttribute: 'permitAll'],
                [url: '/**', configAttribute: 'permitAll'],
        ]
        securedUrls.each {
            if (it.httpMethod) {
                if (!Requestmap.findByUrlAndHttpMethodAndConfigAttribute(it.url, it.httpMethod, it.configAttribute)) {
                    new Requestmap(url: "${it.url}", httpMethod: "${it.httpMethod}", configAttribute: "${it.configAttribute}").save(flush:true)
                }
            }
            else {
                if (!Requestmap.findByUrlAndConfigAttribute(it.url,it.configAttribute)) {
                    new Requestmap(url: "${it.url}", configAttribute: "${it.configAttribute}").save(flush:true)
                }
            }
        }
    }
}
