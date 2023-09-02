package llxbh.zeropointone.dao

import androidx.room.TypeConverter
import java.util.Date

/**
 * 利用的 Room 的 类型转换器 来保存日期
 */
class DateConverter {

    @TypeConverter
    fun dateToLong(value: Date?): Long? {
        return value?.time?.toLong()
    }

    @TypeConverter
    fun longToDate(value: Long?): Date? {
        return value?.let {
            Date(it)
        }
    }

}