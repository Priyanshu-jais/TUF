package com.example.tuf.presentation.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tuf.core.utils.CurrencyFormatter
import com.example.tuf.domain.model.MonthlySummary
import com.example.tuf.ui.theme.*

/**
 * Full-width gradient balance card shown at the top of the Dashboard.
 * Displays total balance, income, and expense with animated counter.
 *
 * @param summary Monthly financial summary.
 * @param isBalanceVisible Whether to show or blur the balance.
 * @param onToggleVisibility Called when the eye icon is tapped.
 * @param currencySymbol Currency symbol to prefix amounts.
 * @param modifier Layout modifier.
 */
@Composable
fun BalanceCard(
    summary: MonthlySummary?,
    isBalanceVisible: Boolean,
    onToggleVisibility: () -> Unit,
    currencySymbol: String = "₹",
    isDark: Boolean = false,
    modifier: Modifier = Modifier
) {
    val gradientColors = if (isDark) {
        listOf(GradientStartDark, GradientEndDark)
    } else {
        listOf(GradientStartLight, GradientEndLight)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.linearGradient(gradientColors),
                shape = MaterialTheme.shapes.extraLarge
            )
            .padding(24.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total Balance",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White.copy(alpha = 0.8f)
                )
                IconButton(
                    onClick = onToggleVisibility,
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color.White.copy(alpha = 0.15f), CircleShape)
                ) {
                    Icon(
                        imageVector = if (isBalanceVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "Toggle balance visibility",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            AnimatedContent(
                targetState = isBalanceVisible,
                transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(300)) },
                label = "balance_toggle"
            ) { visible ->
                if (visible) {
                    CounterText(
                        targetValue = (summary?.balance ?: 0.0).toFloat(),
                        prefix = currencySymbol,
                        style = MaterialTheme.typography.displayMedium,
                        color = Color.White
                    )
                } else {
                    Text(
                        text = "••••••",
                        style = MaterialTheme.typography.displayMedium,
                        color = Color.White,
                        letterSpacing = 4.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                BalanceStat(
                    label = "Income",
                    amount = summary?.totalIncome ?: 0.0,
                    symbol = currencySymbol,
                    isVisible = isBalanceVisible,
                    isPositive = true
                )
                Divider(
                    modifier = Modifier
                        .height(40.dp)
                        .width(1.dp),
                    color = Color.White.copy(alpha = 0.3f)
                )
                BalanceStat(
                    label = "Expense",
                    amount = summary?.totalExpense ?: 0.0,
                    symbol = currencySymbol,
                    isVisible = isBalanceVisible,
                    isPositive = false
                )
            }
        }
    }
}

@Composable
private fun BalanceStat(
    label: String,
    amount: Double,
    symbol: String,
    isVisible: Boolean,
    isPositive: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(Color.White.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isPositive) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                contentDescription = null,
                tint = if (isPositive) Color(0xFF00E5A8) else Color(0xFFFF6B6B),
                modifier = Modifier.size(16.dp)
            )
        }
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = Color.White.copy(alpha = 0.7f))
            Text(
                text = if (isVisible) CurrencyFormatter.format(amount, symbol) else "••••",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}
