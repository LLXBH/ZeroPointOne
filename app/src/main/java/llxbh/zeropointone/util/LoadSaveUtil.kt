package llxbh.zeropointone.util

import android.content.Context
import llxbh.zeropointone.app.appContext

/**
 * 本地数据保存器，负责如何保存某个类型的数据，无需关心用什么保存，怎么保存
 */
class LoadSaveUtil(
    fileName: String,
) {

    val sSharedPreferences = appContext.getSharedPreferences(fileName, Context.MODE_PRIVATE)
    val sEditor = sSharedPreferences.edit()

    fun setInt(key: String, value: Int) {
        sEditor.putInt(key, value)
        sEditor.apply()
    }

    fun getInt(key: String, default: Int): Int {
        return sSharedPreferences.getInt(key, default)
    }

    fun setLong(key: String, value: Long) {
        sEditor.putLong(key, value)
        sEditor.apply()
    }

    fun getLong(key: String, default: Long): Long {
        return sSharedPreferences.getLong(key, default)
    }

    fun setBoolean(key: String, value: Boolean) {
        sEditor.putBoolean(key, value)
        sEditor.apply()
    }

    fun getBoolean(key: String, default: Boolean): Boolean {
        return sSharedPreferences.getBoolean(key, default)
    }


}