package llxbh.zeropointone.data.repository

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import llxbh.zeropointone.data.model.TaskCheck

/**
 * 将 List<Long> 类型转化为 Json 格式在数据库保存
 */
class ListLongConverters {

    @TypeConverter
    fun stringToObject(value: String): List<Long> {
        val listType = object : TypeToken<List<Long>>() {

        }.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun objectToString(list: List<Long>): String {
        return Gson().toJson(list)
    }

}