package com.example.tuf.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tuf.ui.theme.Spacing

/**
 * A section header with a title and optional trailing action.
 *
 * @param title Section title.
 * @param actionLabel Optional trailing clickable label.
 * @param onAction Called when the trailing action is clicked.
 * @param modifier Layout modifier.
 */
@Composable
fun SectionHeader(
    title: String,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = Spacing.md, vertical = Spacing.xs),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        if (actionLabel != null && onAction != null) {
            TextButton(onClick = onAction) {
                Text(
                    text = actionLabel,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

/**
 * A date group separator shown between transaction groups in a list.
 *
 * @param dateLabel Relative date label (e.g. "Today", "12 Apr 2025").
 * @param modifier Layout modifier.
 */
@Composable
fun DateSectionHeader(
    dateLabel: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.md, vertical = Spacing.xs),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        Text(
            text = dateLabel,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        HorizontalDivider(
            modifier = Modifier.weight(1f).padding(start = 12.dp),
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        )
    }
}
