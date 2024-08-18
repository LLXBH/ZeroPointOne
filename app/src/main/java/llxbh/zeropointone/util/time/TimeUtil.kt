package llxbh.zeropointone.util.time

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Calendar
import java.util.Date

/**
 * 时间工具类
 */
object TimeUtil {

    val ONE_DAY_TIMES: Long = 86400000

    @RequiresApi(Build.VERSION_CODES.O)
    private val zoneId = ZoneId.systemDefault()

    /**
     * 将 LocalDate 转换为 Date
     *
     * @param value 本地日期类
     *
     * @return 时间类
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun localDateToDate(value: LocalDate): Date {
        return Date.from(
            value.atStartOfDay()
                .atZone(zoneId)
                .toInstant()
        )
    }

    /**
     * 将 Date 转换为 字符串
     *
     * @param value 时间类
     *
     * @return 日期（yyyy-mm-dd）
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun dateToString(value: Date): String {
        val localDate = value.toInstant()
            .atZone(zoneId)
            .toLocalDate()
        return toString(localDate.year, localDate.monthValue, localDate.dayOfMonth)
    }

    /**
     * 更加给定的年、月、日，输出固定格式的日期
     *
     * @param year 年
     * @param month 月
     * @param day 日
     *
     * @return 日期
     */
    fun toString(year: Int, month: Int, day: Int): String {
        return "$year-${month}-$day"
    }

    /**
     * 将 “字符串” 转换为 Date
     *
     * @param time 时间（yyyy-mm-dd）
     *
     * @return 时间类
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun stringToDate(time: String): Date? {
        val times = time.split("-")
        return try {
            localDateToDate(LocalDate.of(
                times[0].toInt(),
                times[1].toInt(),
                times[2].toInt()
            ))
        } catch (e: Exception) {
            null
        }
    }

    /**
     * 根据给的时间往后增加天数，给出新的时间
     *
     * @param date 时间类
     * @param dayNum 要增加的天数
     *
     * @return 时间类（增加天数后）
     */
    @SuppressLint("NewApi")
    fun onDateOnAddDay(date: Date, dayNum: Int): Date? {
        val date =  date.toInstant()
            .atZone(zoneId)
            .toLocalDate()
            .plusDays(dayNum.toLong())
        return stringToDate(toString(date.year, date.monthValue, date.dayOfMonth))
    }

    /**
     * 获取当前时间的时间戳
     *
     * @return 时间戳
     */
    fun getNowTime(): Long {
        return System.currentTimeMillis()
    }

    /**
     * 根据所给的日期，返回对应的 “时间戳”
     *
     * @param year 年
     * @param month 月
     * @param day 日
     *
     * @return 时间戳
     *
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun getNotTime(year: Int, month: Int, day: Int): Long {
        return getSomeDayStartTimers(LocalDate.of(year, month, day))
    }

    /**
     * 根据需要的增加天数后的时间戳
     *
     * @param times 时间戳
     * @param dayNum 要增加的天数
     *
     * @return 时间戳
     */
    fun getNewTime(times: Long, dayNum: Int): Long {
        return times+(dayNum * ONE_DAY_TIMES)
    }

    /**
     * 将 时间戳 转换为 字符串
     *
     * @param 时间戳
     *
     * @return 日期（yyyy-mm-dd）
     */
    @SuppressLint("NewApi")
    fun timesToString(times: Long): String {
        val date =  Instant.ofEpochMilli(times)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
        return toString(date.year, date.monthValue, date.dayOfMonth)
    }

    /**
     * 将 字符串 转换为 时间戳
     *
     * @param dateTime 日期（yyyy-dd-mm）
     *
     * @return 时间戳
     */
    @SuppressLint("NewApi")
    fun stringToTimes(dateTime: String): Long? {
        return stringToDate(dateTime)?.time
    }

    /**
     * 判断该时间戳是否在今天内
     *
     * @param times 时间戳
     *
     * @return 判断结果
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun isToDay(times: Long): Boolean {
        return isSomeDay(times, LocalDate.now())
    }

    /**
     * 判断一个列表里的时间戳，里面是否包含了 “今天”
     *
     * @param times 时间戳列表
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun isToDay(times: List<Long>): Boolean {
        // 开始时间
        val startOfDayLong = getSomeDayStartTimers(LocalDate.now())
        // 结束时间（加一天然后 - 1）
        val endOfDayLong = getSomeDayStartTimers(LocalDate.now().plusDays(1)) - 1
        // 筛选，如果最后为空，则 False
        return times.filter {
            startOfDayLong <= it
        }.filter {
            it <= endOfDayLong
        }.isNotEmpty()
    }

    /**
     * 判断该时间戳是否在给定的日期内
     *
     * @param times 时间戳
     * @param date 日期
     *
     * @return 判断结果
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun isSomeDay(times: Long, date: LocalDate): Boolean {
        // 开始时间
        val startOfDayLong = getSomeDayStartTimers(date)
        // 结束时间（加一天然后 - 1）
        val endOfDayLong = getSomeDayEndTimers(date)
        // 是否在范围内
        return (startOfDayLong <= times) && (times <= endOfDayLong)
    }

    /**
     * 返回 “当天” 的开始时间
     *
     * @param date 日期
     *
     * @return 时间戳
     *
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun getSomeDayStartTimers(date: LocalDate = LocalDate.now()): Long {
        return date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }

    /**
     * 返回 “当天” 的结束时间
     *
     * @param date 日期
     *
     * @return 时间戳
     *
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun getSomeDayEndTimers(data: LocalDate = LocalDate.now()): Long {
        return getSomeDayStartTimers(data.plusDays(1)) - 1
    }

    /**
     * 将 时间戳 转换为 LocalDate
     *
     * @param times 时间戳
     *
     * @return 时间类
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun getLocalData(times: Long = getNowTime()): LocalDate {
        return Instant.ofEpochMilli(times)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    }

    /**
     * 展示一个日期选择界面
     *
     * @param activity 从哪个页面启动的
     * @param dateSetListener 日期的选择接口，方便在调用处自定义
     * @param time 默认选中的日期
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun showDatePick(
        activity: Activity,
        dateSetListener: DatePickInterface,
        time: LocalDate = getLocalData()
    ) {
        val datePickerView = DatePickerDialog(
            activity,
            0,
            DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                // 月份是从 0 开始，进行修正一下
                dateSetListener.onDateSet(year, month+1, dayOfMonth)
            },
            time.year,
            // 月份是从 0 开始，进行修正一下
            time.monthValue-1,
            time.dayOfMonth
        )
        // 默认周一为一个星期的第一天
        datePickerView.datePicker.firstDayOfWeek = Calendar.MONDAY
        datePickerView.show()
    }

 }