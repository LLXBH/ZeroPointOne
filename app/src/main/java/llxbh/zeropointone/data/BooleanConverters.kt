package llxbh.zeropointone.data

import androidx.room.TypeConverter

/**
 * 数据库没有 Boolean 类型，将其转换为 0 或 1
 */
class BooleanConverters {

    @TypeConverter
    fun booleanToInt(value: Boolean): Int {
        return if (value) {
            1
        } else {
            0
        }
    }

    @TypeConverter
    fun intToBoolean(value: Int): Boolean {
        return value >= 0
    }

}