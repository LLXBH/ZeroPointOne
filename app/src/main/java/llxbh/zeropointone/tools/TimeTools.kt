package llxbh.zeropointone.tools

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import java.lang.Exception
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

object TimeTools {

    val ONE_DAY_TIMES: Long = 86400000

    @RequiresApi(Build.VERSION_CODES.O)
    private val zoneId = ZoneId.systemDefault()

    @RequiresApi(Build.VERSION_CODES.O)
    fun localDateToDate(value: LocalDate): Date {
        return Date.from(
            value.atStartOfDay()
                .atZone(zoneId)
                .toInstant()
        )
    }

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
    fun onDateOnAddDay(date: Date?, dayNum: Int): Date? {
        if (date == null) {
            return null
        }
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

    @SuppressLint("NewApi")
    fun timesToString(times: Long): String {
        val date =  Instant.ofEpochMilli(times)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
        return toString(date.year, date.monthValue, date.dayOfMonth)
    }

    @SuppressLint("NewApi")
    fun stringToTimes(dateTime: String): Long? {
        return stringToDate(dateTime)?.time
    }
 }