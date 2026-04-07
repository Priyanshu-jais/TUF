package com.example.tuf.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * A card with a gradient background.
 *
 * @param gradientColors List of colors for the horizontal gradient.
 * @param modifier Layout modifier.
 * @param cornerRadius Corner radius in dp.
 * @param content Card content.
 */
@Composable
fun GradientCard(
    gradientColors: List<Color>,
    modifier: Modifier = Modifier,
    cornerRadius: androidx.compose.ui.unit.Dp = 24.dp,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .background(
                brush = Brush.linearGradient(gradientColors),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(cornerRadius)
            )
            .padding(0.dp),
        content = content
    )
}
