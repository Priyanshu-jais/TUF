package com.example.tuf.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * A month/year selector with left/right navigation arrows.
 *
 * @param month Current month (1-12).
 * @param year Current year.
 * @param onPrevious Called when left arrow is tapped.
 * @param onNext Called when right arrow is tapped.
 * @param modifier Layout modifier.
 */
@Composable
fun MonthNavigator(
    month: Int,
    year: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cal = Calendar.getInstance()
    cal.set(Calendar.MONTH, month - 1)
    cal.set(Calendar.YEAR, year)
    val sdf = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
    val displayText = sdf.format(cal.time)

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        IconButton(onClick = onPrevious) {
            Icon(
                Icons.Default.ChevronLeft,
                contentDescription = "Previous month",
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Text(
            text = displayText,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        IconButton(onClick = onNext) {
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = "Next month",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}
