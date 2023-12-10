package llxbh.zeropointone.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

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