package ob

import com.ob.FAT

class Son implements FAT{
    String descip
    String status
    static constraints = {
        descip          nullable: true
        status          nullable: true
    }

    @Override
    String name() {
        return null
    }
}
