package ob

import com.ob.FAT

class Man implements FAT{
    String man
    String arm
    static constraints = {
        arm     nullable: true
        man     nullable: true
    }

    @Override
    String name() {
        return null
    }
}
