package com.example.tuf.core.utils

import java.util.Calendar

/**
 * Utility functions for date/time operations that don't belong to extension functions.
 */
object DateUtils {

    /** Returns epoch millis for the first millisecond of the given [month] and [year]. */
    fun startOfMonth(month: Int, year: Int): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, month - 1)
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    /** Returns epoch millis for the last millisecond of the given [month] and [year]. */
    fun endOfMonth(month: Int, year: Int): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, year)
        cal.set(Calendar.MONTH, month - 1)
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        return cal.timeInMillis
    }

    /** Returns current month (1-12). */
    fun currentMonth(): Int = Calendar.getInstance().get(Calendar.MONTH) + 1

    /** Returns current year. */
    fun currentYear(): Int = Calendar.getInstance().get(Calendar.YEAR)

    /** Returns current epoch millis. */
    fun now(): Long = System.currentTimeMillis()

    /** Returns the start of today in epoch millis. */
    fun startOfToday(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    /** Returns the end of today in epoch millis. */
    fun endOfToday(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 999)
        return cal.timeInMillis
    }

    /**
     * Returns a list of (month, year) pairs for the last [count] months including the current.
     */
    fun lastNMonths(count: Int): List<Pair<Int, Int>> {
        val result = mutableListOf<Pair<Int, Int>>()
        val cal = Calendar.getInstance()
        repeat(count) {
            result.add(0, Pair(cal.get(Calendar.MONTH) + 1, cal.get(Calendar.YEAR)))
            cal.add(Calendar.MONTH, -1)
        }
        return result
    }

    /** Returns the next due date epoch millis given the current [dueDate] and [frequency]. */
    fun nextDueDate(dueDate: Long, frequency: String): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = dueDate
        when (frequency) {
            "DAILY" -> cal.add(Calendar.DAY_OF_YEAR, 1)
            "WEEKLY" -> cal.add(Calendar.WEEK_OF_YEAR, 1)
            "MONTHLY" -> cal.add(Calendar.MONTH, 1)
            "YEARLY" -> cal.add(Calendar.YEAR, 1)
        }
        return cal.timeInMillis
    }

    /** Returns epoch millis for the start of the current week (Monday). */
    fun startOfCurrentWeek(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }
}
