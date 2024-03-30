package llxbh.zeropointone.util

import android.content.Context
import llxbh.zeropointone.app.appContext

/**
 * 检测本地的文件，是不是第一次运行app，
 * 如果是第一次运行的话，就填入默认数值，设置好标记，表示已经设置过了
 *
 *
 *
 */
class TomatoClockUtil {

    val FIRST_STARTUP = "FirstStartup"  // 首次启动
    val TIME = "Time"                   // 练习
    val INTERVAL = "Interval"           // 间歇
    val FREQUENCY = "Frequency"         // 组数
    val REST = "Rest"                   // 大休息

    private val sp = appContext.getSharedPreferences("TomatoClock", Context.MODE_PRIVATE)

    init {
        // 判断是否第一次启动，若是则设定默认值
        if (sp.getInt(FIRST_STARTUP, 1) == 1) {
            // 标志已经不是第一次启动
            sp.edit()
                .putInt(FIRST_STARTUP, 0)
                .apply()
            // 设置默认的时间
            setTomato(30, 5, 4,45)
        }
    }

    /**
     * 设置番茄钟
     */
    fun setTomato(time: Int, interval: Int, frequency: Int, rest: Int) {
        sp.edit().apply {
            putInt(FIRST_STARTUP, 0)
            // 默认时间
            putInt(TIME, 30)
            putInt(INTERVAL, 5)
            putInt(FREQUENCY, 4)
            putInt(REST, 45)
            apply()
        }
    }

    fun onStart() {

    }

    fun onStop() {

    }

}