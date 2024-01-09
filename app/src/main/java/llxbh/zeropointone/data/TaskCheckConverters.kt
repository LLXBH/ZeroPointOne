package llxbh.zeropointone.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * 将 TaskCheck 列表转化为 Json 格式在数据库保存
 */
class TaskCheckConverters {

    @TypeConverter
    fun stringToObject(value: String): List<TaskCheck> {
        val listType = object : TypeToken<List<TaskCheck>>() {

        }.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun objectToString(list: List<TaskCheck>): String {
        return Gson().toJson(list)
    }

}