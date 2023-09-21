package llxbh.zeropointone

import android.os.Build
import androidx.annotation.RequiresApi
import java.lang.Exception
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

object TimeTools {

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
        return "$year-${month+1}-$day"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun stringToDate(time: String): Date? {
        val times = time.split("-")
        return try {
            localDateToDate(LocalDate.of(
                times[0].toInt(),
                times[1].toInt()-1,
                times[2].toInt()
            ))
        } catch (e: Exception) {
            null
        }
    }

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

}