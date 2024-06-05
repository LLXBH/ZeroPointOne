package llxbh.zeropointone.util

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import java.lang.Exception
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.Date

object TimeUtil {

    val ONE_DAY_TIMES: Long = 86400000

    @RequiresApi(Build.VERSION_CODES.O)
    private val zoneId = ZoneId.systemDefault()

    /**
     * 将 LocalDate 转换为 Date
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
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun dateToString(value: Date): String {
        val localDate = value.toInstant()
            .atZone(zoneId)
            .toLocalDate()
        return toString(localDate.year, localDate.monthValue, localDate.dayOfMonth)
    }

    fun toString(year: Int, month: Int, day: Int): String {
        return "$year-${month}-$day"
    }

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
     */
    fun getNowTime(): Long {
        return System.currentTimeMillis()
    }

    /**
     * 根据需要的增加天数后的时间戳
     */
    fun getNewTime(times: Long, dayNum: Int): Long {
        return times+(dayNum * ONE_DAY_TIMES)
    }

    /**
     * 将 时间戳 转换为 字符串
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
        val endOfDayLong = getSomeDayStartTimers(date.plusDays(1)) - 1
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
 }