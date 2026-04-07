package com.example.tuf.domain.usecase

import com.example.tuf.core.extensions.toDayOfWeekShort
import com.example.tuf.core.utils.DateUtils
import com.example.tuf.domain.model.CategorySpending
import com.example.tuf.domain.model.DailySpending
import com.example.tuf.domain.model.MonthlyTrend
import com.example.tuf.domain.model.TransactionType
import com.example.tuf.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Returns spending grouped by category for the pie chart.
 */
class GetCategoryWiseSpendingUseCase(private val repository: TransactionRepository) {

    operator fun invoke(month: Int, year: Int): Flow<List<CategorySpending>> {
        return repository.getTransactionsByMonth(month, year).map { transactions ->
            val expenses = transactions.filter { it.type == TransactionType.EXPENSE }
            val total = expenses.sumOf { it.amount }
            if (total == 0.0) return@map emptyList()

            expenses.groupBy { it.category.id }
                .map { (_, txList) ->
                    val category = txList.first().category
                    val amount = txList.sumOf { it.amount }
                    CategorySpending(
                        category = category,
                        amount = amount,
                        percentage = if (total > 0) (amount / total * 100) else 0.0
                    )
                }
                .sortedByDescending { it.amount }
        }
    }
}

/**
 * Returns daily spending for the current week — used for bar chart.
 */
class GetWeeklySpendingUseCase(private val repository: TransactionRepository) {

    operator fun invoke(): Flow<List<DailySpending>> {
        val weekStart = DateUtils.startOfCurrentWeek()
        val weekEnd = weekStart + (7 * 24 * 60 * 60 * 1000L)

        return repository.getTransactionsByDateRange(weekStart, weekEnd).map { transactions ->
            val expenses = transactions.filter { it.type == TransactionType.EXPENSE }

            (0..6).map { dayOffset ->
                val dayStart = weekStart + (dayOffset * 24 * 60 * 60 * 1000L)
                val dayEnd = dayStart + (24 * 60 * 60 * 1000L)
                val dayTotal = expenses
                    .filter { it.date >= dayStart && it.date < dayEnd }
                    .sumOf { it.amount }

                DailySpending(
                    dayLabel = dayStart.toDayOfWeekShort(),
                    date = dayStart,
                    amount = dayTotal
                )
            }
        }
    }
}

/**
 * Returns income vs expense trend for the last 6 months — used for line chart.
 */
class GetMonthlyTrendUseCase(private val repository: TransactionRepository) {

    operator fun invoke(): Flow<List<MonthlyTrend>> {
        val last6Months = DateUtils.lastNMonths(6)
        val monthRangeStart = DateUtils.startOfMonth(last6Months.first().first, last6Months.first().second)
        val monthRangeEnd = DateUtils.endOfMonth(last6Months.last().first, last6Months.last().second)

        return repository.getTransactionsByDateRange(monthRangeStart, monthRangeEnd).map { transactions ->
            val sdf = SimpleDateFormat("MMM", Locale.getDefault())

            last6Months.map { (month, year) ->
                val monthStart = DateUtils.startOfMonth(month, year)
                val monthEnd = DateUtils.endOfMonth(month, year)

                val monthTx = transactions.filter { it.date in monthStart..monthEnd }
                val income = monthTx.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
                val expense = monthTx.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }

                val cal = Calendar.getInstance()
                cal.set(Calendar.MONTH, month - 1)
                val monthLabel = sdf.format(cal.time)

                MonthlyTrend(
                    monthLabel = monthLabel,
                    month = month,
                    year = year,
                    income = income,
                    expense = expense
                )
            }
        }
    }
}
