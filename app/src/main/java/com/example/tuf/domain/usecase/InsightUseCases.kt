package com.example.tuf.domain.usecase

import com.example.tuf.core.utils.Constants
import com.example.tuf.core.utils.DateUtils
import com.example.tuf.domain.model.FinancialHealthScore
import com.example.tuf.domain.model.InsightModel
import com.example.tuf.domain.model.InsightType
import com.example.tuf.domain.model.TransactionType
import com.example.tuf.domain.repository.BudgetRepository
import com.example.tuf.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * Generates auto-insights for the Dashboard based on spending patterns.
 */
class GetInsightsUseCase(private val repository: TransactionRepository) {

    operator fun invoke(month: Int, year: Int): Flow<List<InsightModel>> {
        return repository.getTransactionsByMonth(month, year).map { transactions ->
            val insights = mutableListOf<InsightModel>()
            val expenses = transactions.filter { it.type == TransactionType.EXPENSE }
            val income = transactions.filter { it.type == TransactionType.INCOME }

            // Insight: Top spending category
            val topCategory = expenses
                .groupBy { it.category.name }
                .mapValues { (_, txList) -> txList.sumOf { it.amount } }
                .maxByOrNull { it.value }

            if (topCategory != null) {
                val totalExpense = expenses.sumOf { it.amount }
                val pct = if (totalExpense > 0) (topCategory.value / totalExpense * 100).toInt() else 0
                insights.add(
                    InsightModel(
                        emoji = "🏆",
                        title = "Top Category: ${topCategory.key}",
                        description = "${topCategory.key} is your biggest expense at $pct% of total spending.",
                        type = InsightType.NEUTRAL
                    )
                )
            }

            // Insight: Weekend vs weekday spending
            val weekdayTotal = expenses
                .filter { tx ->
                    val cal = Calendar.getInstance()
                    cal.timeInMillis = tx.date
                    cal.get(Calendar.DAY_OF_WEEK) !in listOf(Calendar.SATURDAY, Calendar.SUNDAY)
                }
                .sumOf { it.amount }
            val weekendTotal = expenses.sumOf { it.amount } - weekdayTotal

            if (weekendTotal > weekdayTotal * 0.5) {
                insights.add(
                    InsightModel(
                        emoji = "📅",
                        title = "Weekend Spender",
                        description = "You tend to spend more on weekends. Consider planning weekend activities in advance.",
                        type = InsightType.WARNING
                    )
                )
            }

            // Insight: Savings
            val totalIncome = income.sumOf { it.amount }
            val totalExpenseAmt = expenses.sumOf { it.amount }
            if (totalIncome > 0) {
                val savingsRate = ((totalIncome - totalExpenseAmt) / totalIncome * 100).toInt()
                when {
                    savingsRate >= 20 -> insights.add(
                        InsightModel(
                            emoji = "🎉",
                            title = "Great Savings!",
                            description = "You saved $savingsRate% of your income this month. Keep it up!",
                            type = InsightType.POSITIVE
                        )
                    )
                    savingsRate < 0 -> insights.add(
                        InsightModel(
                            emoji = "⚠️",
                            title = "Overspending Alert",
                            description = "Your expenses exceeded your income this month. Review your spending.",
                            type = InsightType.NEGATIVE
                        )
                    )
                }
            }

            insights.take(3)
        }
    }
}

/**
 * Calculates a Financial Health Score (0–100) based on savings rate, budget adherence, and regularity.
 */
class GetFinancialHealthScoreUseCase(
    private val transactionRepository: TransactionRepository,
    private val budgetRepository: BudgetRepository
) {
    operator fun invoke(month: Int, year: Int): Flow<FinancialHealthScore> {
        val transactionsFlow = transactionRepository.getTransactionsByMonth(month, year)
        val budgetsFlow = budgetRepository.getBudgetsByMonth(month, year)

        return combine(transactionsFlow, budgetsFlow) { transactions, budgets ->
            val income = transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
            val expense = transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }

            // Savings score: 0–40 points
            val savingsRate = if (income > 0) (income - expense) / income else 0.0
            val savingsScore = (savingsRate.coerceIn(0.0, 1.0) * 40).toFloat()

            // Budget adherence score: 0–40 points
            val budgetScore = if (budgets.isEmpty()) {
                20f // Neutral if no budgets set
            } else {
                val categorySpending = transactions.filter { it.type == TransactionType.EXPENSE }
                    .groupBy { it.category.id }
                    .mapValues { (_, txList) -> txList.sumOf { it.amount } }

                val adherenceRates = budgets.map { budget ->
                    val spent = categorySpending[budget.category.id] ?: 0.0
                    if (budget.limitAmount > 0) (1.0 - (spent / budget.limitAmount)).coerceIn(0.0, 1.0) else 1.0
                }
                (adherenceRates.average() * 40).toFloat()
            }

            // Regularity score: 0–20 points (based on number of transactions in month)
            val regularityScore = (transactions.size.coerceAtMost(20) / 20.0 * 20).toFloat()

            val totalScore = (savingsScore + budgetScore + regularityScore).toInt().coerceIn(0, 100)

            FinancialHealthScore(
                score = totalScore,
                savingsScore = savingsScore,
                budgetScore = budgetScore,
                regularityScore = regularityScore
            )
        }
    }
}
