package com.example.tuf.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tuf.core.utils.CurrencyFormatter
import com.example.tuf.domain.model.BudgetProgress
import com.example.tuf.ui.theme.*

/**
 * A card displaying budget progress for a single category.
 *
 * @param progress The budget progress data.
 * @param currencySymbol Currency symbol.
 * @param onClick Called when the card is tapped.
 * @param modifier Layout modifier.
 */
@Composable
fun BudgetProgressCard(
    progress: BudgetProgress,
    currencySymbol: String = "₹",
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val percentage = progress.percentage.coerceIn(0f, 1.5f)
    val progressColor = when {
        progress.isOverBudget -> MaterialTheme.colorScheme.error
        percentage > 0.79f -> Color(0xFFFFC107)
        else -> MaterialTheme.colorScheme.tertiary
    }

    val animatedProgress by animateFloatAsState(
        targetValue = percentage.coerceIn(0f, 1f),
        animationSpec = tween(800),
        label = "budget_progress"
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(modifier = Modifier.padding(Spacing.md)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(progress.budget.category.color, RoundedCornerShape(50))
                    )
                    Text(
                        progress.budget.category.name,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                if (progress.isOverBudget) {
                    Surface(
                        color = MaterialTheme.colorScheme.error.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            "Over budget!",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = progressColor,
                trackColor = progressColor.copy(alpha = 0.15f)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    CurrencyFormatter.format(progress.spent, currencySymbol),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                Text(
                    "of ${CurrencyFormatter.format(progress.budget.limitAmount, currencySymbol)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}
