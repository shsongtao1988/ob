package com.ob

class Debt {
    String name
    String price
    String status

    static constraints = {
        name    nullable: true
        price   nullable: true
        status  nullable: true
    }

    @Override
    String toString() {
        return this.name+" "+this.price+" "+this.status
    }
}
