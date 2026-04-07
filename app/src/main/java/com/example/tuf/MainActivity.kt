package com.example.tuf

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.tuf.core.utils.Constants
import com.example.tuf.data.local.DataStoreManager
import com.example.tuf.domain.usecase.ProcessDueRecurringUseCase
import com.example.tuf.presentation.navigation.AppScaffold
import com.example.tuf.presentation.navigation.Screen
import com.example.tuf.ui.theme.FinanceManagerTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.android.inject

/**
 * Main entry point for the Finance Manager application.
 * Handles splash screen, theme initialization, onboarding check,
 * and recurring transaction processing.
 */
class MainActivity : ComponentActivity() {

    private val dataStoreManager: DataStoreManager by inject()
    private val processDueRecurringUseCase: ProcessDueRecurringUseCase by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        // Process any overdue recurring transactions in the background
        CoroutineScope(Dispatchers.IO).launch {
            try { processDueRecurringUseCase() } catch (e: Exception) { /* silently ignore on startup */ }
        }

        // Determine start destination based on onboarding completion
        val isOnboardingCompleted = runBlocking {
            dataStoreManager.isOnboardingCompleted.first()
        }
        val startDestination = if (isOnboardingCompleted) Screen.Dashboard.route else Screen.Onboarding.route

        // Determine initial theme
        val initialThemeMode = runBlocking {
            dataStoreManager.themeMode.first()
        }

        setContent {
            var themeMode by remember { mutableStateOf(initialThemeMode) }
            val systemDark = isSystemInDarkTheme()
            val isDark = when (themeMode) {
                Constants.THEME_DARK -> true
                Constants.THEME_LIGHT -> false
                else -> systemDark
            }

            LaunchedEffect(isDark) {
                enableEdgeToEdge(
                    statusBarStyle = if (isDark) {
                        SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
                    } else {
                        SystemBarStyle.light(android.graphics.Color.TRANSPARENT, android.graphics.Color.TRANSPARENT)
                    }
                )
            }

            FinanceManagerTheme(darkTheme = isDark) {
                AppScaffold(
                    startDestination = startDestination,
                    onThemeToggle = {
                        themeMode = if (isDark) Constants.THEME_LIGHT else Constants.THEME_DARK
                        CoroutineScope(Dispatchers.IO).launch {
                            dataStoreManager.setThemeMode(themeMode)
                        }
                    }
                )
            }
        }
    }
}