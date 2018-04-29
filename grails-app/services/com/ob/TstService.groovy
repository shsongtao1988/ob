package com.ob

import grails.transaction.Transactional

@Transactional
class TstService {

    def serviceMethod() { }

    def test(){
        println "just to test .."
    }
}
