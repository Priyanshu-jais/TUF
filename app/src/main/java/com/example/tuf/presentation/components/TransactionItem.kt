package com.example.tuf.presentation.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.tuf.core.extensions.toFormattedDate
import com.example.tuf.core.extensions.toFormattedTime
import com.example.tuf.core.utils.CurrencyFormatter
import com.example.tuf.domain.model.Transaction
import com.example.tuf.domain.model.TransactionType
import com.example.tuf.ui.theme.*

/**
 * A single transaction row composable for use in lists.
 *
 * @param transaction The transaction to display.
 * @param currencySymbol Currency symbol prefix.
 * @param onClick Called when the item is tapped.
 * @param modifier Layout modifier.
 */
@Composable
fun TransactionItem(
    transaction: Transaction,
    currencySymbol: String = "₹",
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val amountColor = if (transaction.type == TransactionType.INCOME) {
        MaterialTheme.colorScheme.tertiary
    } else {
        MaterialTheme.colorScheme.error
    }

    val amountPrefix = if (transaction.type == TransactionType.INCOME) "+" else "-"

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        // Category icon circle
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(transaction.category.color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = getCategoryEmoji(transaction.category.iconName),
                style = MaterialTheme.typography.titleMedium
            )
        }

        // Title + subtitle
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = if (transaction.note.isNotBlank()) transaction.note
                else transaction.category.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "${transaction.category.name} • ${transaction.date.toFormattedTime()}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Amount
        Text(
            text = "$amountPrefix${CurrencyFormatter.format(transaction.amount, currencySymbol)}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = amountColor
        )
    }
}

/** Maps icon name strings to emoji representations for display. */
fun getCategoryEmoji(iconName: String): String = when (iconName) {
    "restaurant" -> "🍔"
    "directions_car" -> "🚗"
    "shopping_bag" -> "🛍️"
    "movie" -> "🎬"
    "local_hospital" -> "💊"
    "bolt" -> "⚡"
    "school" -> "📚"
    "flight" -> "✈️"
    "home" -> "🏠"
    "spa" -> "💆"
    "work" -> "💼"
    "laptop" -> "💻"
    "trending_up" -> "📈"
    "business" -> "🏢"
    "card_giftcard" -> "🎁"
    "category" -> "➕"
    else -> "💰"
}
