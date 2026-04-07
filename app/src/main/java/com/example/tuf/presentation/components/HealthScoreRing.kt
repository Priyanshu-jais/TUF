package com.example.tuf.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tuf.domain.model.FinancialHealthScore

/**
 * An animated donut ring showing the [FinancialHealthScore].
 * The ring color transitions from red → yellow → green based on score.
 *
 * @param score The financial health score (0-100).
 * @param modifier Layout modifier.
 * @param ringSize Ring diameter in dp.
 * @param strokeWidth Ring thickness in dp.
 */
@Composable
fun HealthScoreRing(
    score: FinancialHealthScore?,
    modifier: Modifier = Modifier,
    ringSize: Float = 160f,
    strokeWidth: Float = 20f
) {
    val targetScore = (score?.score ?: 0).toFloat()

    val animatedSweep by animateFloatAsState(
        targetValue = (targetScore / 100f) * 270f,
        animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
        label = "health_ring"
    )

    val ringColor = when {
        targetScore >= 80 -> Color(0xFF00C897)
        targetScore >= 60 -> Color(0xFF4CAF50)
        targetScore >= 40 -> Color(0xFFFFC107)
        else -> Color(0xFFFF4757)
    }

    Box(
        modifier = modifier.size(ringSize.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidthPx = strokeWidth.dp.toPx()
            val arcSize = Size(size.width - strokeWidthPx, size.height - strokeWidthPx)
            val arcOffset = Offset(strokeWidthPx / 2, strokeWidthPx / 2)

            // Background ring
            drawArc(
                color = ringColor.copy(alpha = 0.15f),
                startAngle = 135f,
                sweepAngle = 270f,
                useCenter = false,
                topLeft = arcOffset,
                size = arcSize,
                style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
            )

            // Score ring
            drawArc(
                color = ringColor,
                startAngle = 135f,
                sweepAngle = animatedSweep,
                useCenter = false,
                topLeft = arcOffset,
                size = arcSize,
                style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${score?.score ?: 0}",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = ringColor
            )
            Text(
                text = score?.label ?: "—",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}
