package com.ob

/**
 * Created by songtao on 2018/4/17.
 */
class Delay implements Runnable{
    Map map
    TstService tstService

    @Override
    void run() {
        println "in delayed service now start to work .."
        String type = map.get("type")
        println "type is ... "+type
        if(!type){
            return
        }
        Object obj = map.get("object")
        switch (type){
            case "debt":
                println new Date().toTimestamp()
                sleep(1000)
                tstService.test()
                println "sleep 1s"
                println new Date().toTimestamp()
                break;
            case "asset":
                sleep(10)
                println "sleep 10"
                def debt = obj as Debt
                println debt
                break;
            case "assetPack":
                sleep(10)
                println "sleep 10"
                break
            case "debtPack":
                sleep(10)
                println "sleep 10"
                break
            case  "company":
                sleep(10)
                println "sleep 10"
                break
            case "companyPack":
                sleep(10)
                println "sleep 10"
                break
            default:
                sleep(10)
                println "sleep 10"
                break
        }
    }

}
