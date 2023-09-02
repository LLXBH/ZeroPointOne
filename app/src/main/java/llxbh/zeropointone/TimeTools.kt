package llxbh.zeropointone

import android.os.Build
import androidx.annotation.RequiresApi
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

}