package com.ob

import com.ob.user.User
import grails.rest.RestfulController

class DebtController extends RestfulController{
    static responseFormats = ['json','xml']
    DebtController(){
        super(Debt)
    }

    def noAuth(){
        render "no need auth"
    }

    def needAuth(){
        render "need auth"
    }

    def meth(){
        render "this is a method"
    }

    def debtService

    def testToken(){
        User user = User.findById(1)
        String token = debtService.genUserToken(user)
        println token
        render "KO"
    }
    def tObserver(){
        println "in test observer"
        Debt debt= new Debt(name:"name",status: "status1",price: "price of all")
        debtService.debtDelay(debt)
        render "OK"
    }

}
