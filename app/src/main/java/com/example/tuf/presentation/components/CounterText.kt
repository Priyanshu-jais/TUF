package com.example.tuf.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.runtime.*
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight

/**
 * Animates a number count-up from 0 to [targetValue].
 * Used for the balance display on the Dashboard.
 *
 * @param targetValue The final value to animate to.
 * @param prefix Text to show before the number (e.g. "₹").
 * @param style Text style for the display.
 * @param color Text color.
 * @param modifier Layout modifier.
 */
@Composable
fun CounterText(
    targetValue: Float,
    prefix: String = "₹",
    style: TextStyle = MaterialTheme.typography.displayMedium,
    color: Color = MaterialTheme.colorScheme.onSurface,
    modifier: Modifier = Modifier
) {
    val animatedValue by animateFloatAsState(
        targetValue = targetValue,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "counter_animation"
    )

    val displayText = buildString {
        append(prefix)
        val value = animatedValue.toDouble()
        if (value % 1 == 0.0) {
            append("%,.0f".format(value))
        } else {
            append("%,.2f".format(value))
        }
    }

    Text(
        text = displayText,
        style = style,
        fontWeight = FontWeight.Bold,
        color = color,
        modifier = modifier
    )
}
