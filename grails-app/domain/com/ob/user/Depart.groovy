package com.ob.user

class Depart {

    String name     //部门名称
    Long level      //级别

    static hasMany = [
            departs : Depart        //该部门的子部门
    ]

    static constraints = {
        name nullable: true
        level nullable: true
    }
}
