package com.example.tuf.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tuf.domain.model.InsightModel
import com.example.tuf.domain.model.InsightType
import com.example.tuf.ui.theme.Spacing

/**
 * A card displaying an auto-generated financial insight.
 * Uses a gradient background based on the insight type.
 *
 * @param insight The insight to display.
 * @param onDismiss Called when the dismiss button is tapped.
 * @param modifier Layout modifier.
 */
@Composable
fun InsightCard(
    insight: InsightModel,
    onDismiss: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val (gradientStart, gradientEnd) = when (insight.type) {
        InsightType.POSITIVE -> Pair(Color(0xFF00C897), Color(0xFF00A878))
        InsightType.NEGATIVE -> Pair(Color(0xFFFF4757), Color(0xFFFF6584))
        InsightType.WARNING -> Pair(Color(0xFFFF9800), Color(0xFFFFC107))
        InsightType.NEUTRAL -> Pair(Color(0xFF6C63FF), Color(0xFF9B8FFF))
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.linearGradient(listOf(gradientStart, gradientEnd)),
                shape = MaterialTheme.shapes.large
            )
            .padding(Spacing.md)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = insight.emoji,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(end = Spacing.md, top = 2.dp)
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = insight.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = insight.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
        }
    }
}
