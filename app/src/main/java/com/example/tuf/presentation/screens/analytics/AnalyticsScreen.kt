package com.example.tuf.presentation.screens.analytics

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tuf.core.utils.CurrencyFormatter
import com.example.tuf.data.local.DataStoreManager
import com.example.tuf.domain.model.CategorySpending
import com.example.tuf.domain.model.DailySpending
import com.example.tuf.domain.model.MonthlyTrend
import com.example.tuf.presentation.components.CounterText
import com.example.tuf.presentation.components.EmptyState
import com.example.tuf.presentation.components.SectionHeader
import com.example.tuf.ui.theme.Spacing
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

/**
 * Analytics screen showing charts for spending breakdown, monthly trends, and weekly spending.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel = koinViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val currencySymbol by koinInject<DataStoreManager>().currencySymbol.collectAsState(initial = "₹")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Analytics", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 32.dp)
        ) {
            // Summary stats row
            uiState.summary?.let { summary ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Spacing.md),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    StatCard("Income", summary.totalIncome, currencySymbol, Color(0xFF00C897), Modifier.weight(1f))
                    StatCard("Expense", summary.totalExpense, currencySymbol, MaterialTheme.colorScheme.error, Modifier.weight(1f))
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.md),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    StatCard("Balance", summary.balance, currencySymbol, MaterialTheme.colorScheme.primary, Modifier.weight(1f))
                    StatCard("Savings", summary.savingsRate, currencySymbol, Color(0xFFFFC107), Modifier.weight(1f), isSavings = true)
                }
            }

            // Category Spending Pie Chart (simplified)
            SectionHeader(title = "Spending by Category", modifier = Modifier.padding(top = Spacing.md))

            if (uiState.categorySpending.isEmpty()) {
                EmptyState(title = "No expense data", emoji = "📊", subtitle = "Add expenses to see breakdown")
            } else {
                CategorySpendingList(
                    items = uiState.categorySpending,
                    currencySymbol = currencySymbol,
                    modifier = Modifier.padding(horizontal = Spacing.md)
                )
            }

            // Weekly spending bar chart
            SectionHeader(title = "This Week's Spending", modifier = Modifier.padding(top = Spacing.md))
            WeeklyBarChart(
                data = uiState.weeklySpending,
                currencySymbol = currencySymbol,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .padding(horizontal = Spacing.md)
            )

            // Monthly trend
            SectionHeader(title = "6-Month Trend", modifier = Modifier.padding(top = Spacing.md))
            MonthlyTrendChart(
                data = uiState.monthlyTrend,
                currencySymbol = currencySymbol,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(horizontal = Spacing.md)
            )
        }
    }
}

@Composable
private fun StatCard(
    label: String,
    value: Double,
    currencySymbol: String,
    color: Color,
    modifier: Modifier = Modifier,
    isSavings: Boolean = false
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.08f)),
        shape = MaterialTheme.shapes.large
    ) {
        Column(modifier = Modifier.padding(Spacing.md)) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = color)
            Spacer(Modifier.height(4.dp))
            if (isSavings) {
                Text(
                    "${value.toInt()}%",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            } else {
                CounterText(
                    targetValue = value.toFloat(),
                    prefix = currencySymbol,
                    style = MaterialTheme.typography.headlineSmall,
                    color = color
                )
            }
        }
    }
}

@Composable
private fun CategorySpendingList(
    items: List<CategorySpending>,
    currencySymbol: String,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        items.take(8).forEach { item ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(item.category.color)
                )
                Text(item.category.name, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                LinearProgressIndicator(
                    progress = { (item.percentage / 100).toFloat().coerceIn(0f, 1f) },
                    modifier = Modifier.width(80.dp).height(6.dp),
                    color = item.category.color,
                    trackColor = item.category.color.copy(alpha = 0.15f)
                )
                Text(
                    CurrencyFormatter.format(item.amount, currencySymbol),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun WeeklyBarChart(
    data: List<DailySpending>,
    currencySymbol: String,
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) return
    val maxAmount = data.maxOf { it.amount }.coerceAtLeast(1.0)
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        data.forEach { daily ->
            val heightFraction = (daily.amount / maxAmount).toFloat()
            val animatedHeight by animateFloatAsState(targetValue = heightFraction, animationSpec = tween(800), label = "bar")
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.6f)
                        .fillMaxHeight(animatedHeight.coerceAtLeast(0.02f))
                        .clip(androidx.compose.foundation.shape.RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                        .background(MaterialTheme.colorScheme.primary)
                )
                Spacer(Modifier.height(4.dp))
                Text(daily.dayLabel, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
private fun MonthlyTrendChart(
    data: List<MonthlyTrend>,
    currencySymbol: String,
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) return
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = MaterialTheme.shapes.large
    ) {
        Column(modifier = Modifier.padding(Spacing.md)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(Modifier.size(10.dp).clip(CircleShape).background(Color(0xFF00C897)))
                    Text("Income", style = MaterialTheme.typography.labelSmall)
                }
                Spacer(Modifier.width(Spacing.md))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(Modifier.size(10.dp).clip(CircleShape).background(MaterialTheme.colorScheme.error))
                    Text("Expense", style = MaterialTheme.typography.labelSmall)
                }
            }
            Spacer(Modifier.height(Spacing.sm))
            val maxHeight = 120.dp
            Row(
                modifier = Modifier.fillMaxWidth().height(maxHeight),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                val maxVal = data.maxOf { maxOf(it.income, it.expense) }.coerceAtLeast(1.0)
                data.forEach { trend ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                        val incomeH by animateFloatAsState((trend.income / maxVal).toFloat().coerceAtLeast(0.02f), tween(800), label = "inc")
                        val expH by animateFloatAsState((trend.expense / maxVal).toFloat().coerceAtLeast(0.02f), tween(800), label = "exp")
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(2.dp),
                            verticalAlignment = Alignment.Bottom,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(Modifier.width(10.dp).fillMaxHeight(incomeH).clip(androidx.compose.foundation.shape.RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)).background(Color(0xFF00C897)))
                            Box(Modifier.width(10.dp).fillMaxHeight(expH).clip(androidx.compose.foundation.shape.RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)).background(MaterialTheme.colorScheme.error))
                        }
                        Text(trend.monthLabel, style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}
