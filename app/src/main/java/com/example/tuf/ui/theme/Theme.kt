package com.example.tuf.ui.theme

import android.app.Activity
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = PrimaryLight,
    onPrimary = OnPrimaryLight,
    secondary = SecondaryLight,
    onSecondary = OnSecondaryLight,
    background = BackgroundLight,
    onBackground = OnBackgroundLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    outline = OutlineLight,
    error = ErrorColor,
    tertiary = IncomeLight
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    onPrimary = OnPrimaryDark,
    secondary = SecondaryDark,
    onSecondary = OnSecondaryDark,
    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    outline = OutlineDark,
    error = ExpenseDark,
    tertiary = IncomeDark
)

/**
 * Main application theme with animated dark/light mode switching via [Crossfade].
 *
 * @param darkTheme Whether to use dark mode. If null, follows system setting.
 * @param content The composable content to display within this theme.
 */
@Composable
fun FinanceManagerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    Crossfade(
        targetState = darkTheme,
        animationSpec = tween(durationMillis = 400),
        label = "theme_crossfade"
    ) { isDark ->
        MaterialTheme(
            colorScheme = if (isDark) DarkColorScheme else LightColorScheme,
            typography = FinanceTypography,
            shapes = FinanceShapes,
            content = content
        )
    }
}

/** Backward-compatible alias kept for generated code compatibility. */
@Composable
fun TUFTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) = FinanceManagerTheme(darkTheme = darkTheme, content = content)