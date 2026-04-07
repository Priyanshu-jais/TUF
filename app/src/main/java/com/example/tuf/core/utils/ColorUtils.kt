package com.example.tuf.core.utils

import androidx.compose.ui.graphics.Color

/**
 * Utility functions for working with colors in the Finance Manager app.
 */
object ColorUtils {

    /**
     * Converts a hex color string (e.g. "#FF6584" or "FF6584") to a Compose [Color].
     * Returns [Color.Gray] if parsing fails.
     */
    fun hexToColor(hex: String): Color {
        return try {
            val cleanHex = hex.removePrefix("#")
            val fullHex = when (cleanHex.length) {
                6 -> "FF$cleanHex"
                8 -> cleanHex
                else -> "FFAAAAAA"
            }
            Color(android.graphics.Color.parseColor("#$fullHex"))
        } catch (e: Exception) {
            Color.Gray
        }
    }

    /**
     * Converts a Compose [Color] to a hex string (e.g. "#FF6584").
     */
    fun colorToHex(color: Color): String {
        val argb = color.hashCode()
        return "#%08X".format(argb and 0xFFFFFFFF.toInt())
    }

    /**
     * Returns a contrasting text color (black or white) for a given background [Color].
     */
    fun contrastColor(background: Color): Color {
        val luminance = 0.299 * background.red + 0.587 * background.green + 0.114 * background.blue
        return if (luminance > 0.5f) Color(0xFF1A1A2E) else Color.White
    }
}

/** Extension to convert a hex string to Compose [Color]. */
fun String.toComposeColor(): Color = ColorUtils.hexToColor(this)
