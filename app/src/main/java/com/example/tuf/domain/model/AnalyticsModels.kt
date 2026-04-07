package com.example.tuf.domain.model

/**
 * Aggregated financial summary for a specific month.
 *
 * @property month Month (1-12).
 * @property year Full year.
 * @property totalIncome Total income for this month.
 * @property totalExpense Total expense for this month.
 * @property balance Net balance (income - expense).
 * @property savingsRate Savings as a percentage of income (0–100).
 */
data class MonthlySummary(
    val month: Int,
    val year: Int,
    val totalIncome: Double,
    val totalExpense: Double,
    val balance: Double = totalIncome - totalExpense,
    val savingsRate: Double = if (totalIncome > 0) ((totalIncome - totalExpense) / totalIncome * 100).coerceAtLeast(0.0) else 0.0
)

/**
 * Spending data for a single category — used for pie chart and ranked list.
 *
 * @property category The category.
 * @property amount Total amount spent in this category.
 * @property percentage Fraction of total spending (0.0–1.0).
 */
data class CategorySpending(
    val category: Category,
    val amount: Double,
    val percentage: Double
)

/**
 * A day-level spending data point — used for bar charts.
 *
 * @property dayLabel Short day name (e.g. "Mon").
 * @property date Epoch millis for that day.
 * @property amount Total spending on that day.
 */
data class DailySpending(
    val dayLabel: String,
    val date: Long,
    val amount: Double
)

/**
 * Month-level trend data point — used for line charts.
 *
 * @property monthLabel Short month label (e.g. "Apr").
 * @property income Total income.
 * @property expense Total expense.
 */
data class MonthlyTrend(
    val monthLabel: String,
    val month: Int,
    val year: Int,
    val income: Double,
    val expense: Double
)

/**
 * Auto-generated insight card shown on the Dashboard.
 *
 * @property emoji Display emoji.
 * @property title Short headline.
 * @property description Detailed insight text.
 * @property type Used for icon/color differentiation.
 */
data class InsightModel(
    val emoji: String,
    val title: String,
    val description: String,
    val type: InsightType = InsightType.NEUTRAL
)

enum class InsightType { POSITIVE, NEGATIVE, NEUTRAL, WARNING }

/**
 * Financial health score (0–100) computed from savings rate, budget adherence, and regularity.
 *
 * @property score Overall score.
 * @property savingsScore Sub-score from savings rate.
 * @property budgetScore Sub-score from budget adherence.
 * @property regularityScore Sub-score from transaction regularity.
 * @property label Human-readable label (e.g. "Excellent", "Good", "Fair", "Poor").
 */
data class FinancialHealthScore(
    val score: Int,
    val savingsScore: Float,
    val budgetScore: Float,
    val regularityScore: Float
) {
    val label: String get() = when {
        score >= 80 -> "Excellent"
        score >= 60 -> "Good"
        score >= 40 -> "Fair"
        else -> "Needs Work"
    }
}
