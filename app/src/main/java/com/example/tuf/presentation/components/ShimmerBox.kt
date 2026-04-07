package com.example.tuf.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * A shimmer-effect placeholder composable for loading states.
 *
 * @param modifier Layout modifier.
 * @param shape Optional corner shape for the shimmer box.
 */
@Composable
fun ShimmerBox(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(12.dp)
) {
    val shimmerColors = listOf(
        Color.LightGray.copy(alpha = 0.6f),
        Color.LightGray.copy(alpha = 0.2f),
        Color.LightGray.copy(alpha = 0.6f)
    )

    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_translate"
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(x = translateAnim - 500f, y = 0f),
        end = Offset(x = translateAnim, y = 0f)
    )

    Box(
        modifier = modifier
            .clip(shape)
            .background(brush)
    )
}

/** Shimmer placeholder for a transaction card row. */
@Composable
fun TransactionItemShimmer() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ShimmerBox(modifier = Modifier.size(42.dp), shape = RoundedCornerShape(50))
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            ShimmerBox(modifier = Modifier.fillMaxWidth(0.6f).height(14.dp))
            ShimmerBox(modifier = Modifier.fillMaxWidth(0.4f).height(12.dp))
        }
        ShimmerBox(modifier = Modifier.width(64.dp).height(16.dp))
    }
}

/** Full-screen shimmer placeholder for dashboard loading state. */
@Composable
fun DashboardShimmer() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        ShimmerBox(modifier = Modifier.fillMaxWidth().height(180.dp), shape = RoundedCornerShape(24.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            repeat(4) {
                ShimmerBox(modifier = Modifier.size(72.dp).weight(1f), shape = RoundedCornerShape(16.dp))
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        repeat(5) {
            TransactionItemShimmer()
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}
