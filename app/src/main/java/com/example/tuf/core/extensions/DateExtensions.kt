package com.example.tuf.core.extensions

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/** Converts epoch milliseconds to a formatted date string (e.g. "12 Apr 2025"). */
fun Long.toFormattedDate(): String {
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return sdf.format(Date(this))
}

/** Converts epoch milliseconds to a relative label: "Today", "Yesterday", or formatted date. */
fun Long.toRelativeDateLabel(): String {
    val cal = Calendar.getInstance()
    val today = cal.clone() as Calendar
    val yesterday = cal.clone() as Calendar
    yesterday.add(Calendar.DAY_OF_YEAR, -1)

    cal.timeInMillis = this
    return when {
        isSameDay(cal, today) -> "Today"
        isSameDay(cal, yesterday) -> "Yesterday"
        else -> toFormattedDate()
    }
}

private fun isSameDay(cal1: Calendar, cal2: Calendar): Boolean =
    cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
            cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)

/** Converts epoch milliseconds to time string (e.g. "02:30 PM"). */
fun Long.toFormattedTime(): String {
    val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return sdf.format(Date(this))
}

/** Returns the month integer (1-12) from epoch milliseconds. */
fun Long.toMonth(): Int {
    val cal = Calendar.getInstance()
    cal.timeInMillis = this
    return cal.get(Calendar.MONTH) + 1
}

/** Returns the year integer from epoch milliseconds. */
fun Long.toYear(): Int {
    val cal = Calendar.getInstance()
    cal.timeInMillis = this
    return cal.get(Calendar.YEAR)
}

/** Returns "MMM yyyy" string (e.g. "Apr 2025"). */
fun Long.toMonthYear(): String {
    val sdf = SimpleDateFormat("MMM yyyy", Locale.getDefault())
    return sdf.format(Date(this))
}

/** Returns "EEE" string (e.g. "Mon"). */
fun Long.toDayOfWeekShort(): String {
    val sdf = SimpleDateFormat("EEE", Locale.getDefault())
    return sdf.format(Date(this))
}

/** Returns epoch millis for start of day (00:00:00). */
fun Long.toStartOfDay(): Long {
    val cal = Calendar.getInstance()
    cal.timeInMillis = this
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)
    return cal.timeInMillis
}

/** Returns epoch millis for end of day (23:59:59). */
fun Long.toEndOfDay(): Long {
    val cal = Calendar.getInstance()
    cal.timeInMillis = this
    cal.set(Calendar.HOUR_OF_DAY, 23)
    cal.set(Calendar.MINUTE, 59)
    cal.set(Calendar.SECOND, 59)
    cal.set(Calendar.MILLISECOND, 999)
    return cal.timeInMillis
}
