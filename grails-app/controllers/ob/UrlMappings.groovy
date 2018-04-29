package ob

class UrlMappings {

    static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }
        '/api/spec/debt-meth'(controller: 'debt',action: 'meth')
        '/api/user/debt-auth'(controller: 'debt',action: 'needAuth')
        '/api/dw/debt'(resources: 'debt')
        '/all/debt'(resources: 'debt')
        '/api/dw/loginPage'(controller: 'wechatOauth',action: 'wxLoginPage')
        '/api/dw/wechat'(controller: 'wechatOauth',action: 'wechat')
        '/all/debt/testToken'(controller: 'debt',action: 'testToken')
        '/all/debt/testOb'(controller: 'debt',action: 'tObserver')

        "/"(view:"/index")
        "500"(view:'/error')
        "404"(view:'/notFound')
    }
}
