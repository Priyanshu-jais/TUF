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

        setContent {
            val themeMode by dataStoreManager.themeMode.collectAsState(initial = Constants.THEME_SYSTEM)
            val isLoggedInState by dataStoreManager.isLoggedIn.collectAsState(initial = null)
            val isOnboardingCompletedState by dataStoreManager.isOnboardingCompleted.collectAsState(initial = null)
            
            // Show splash screen block until initial state is resolved
            if (isLoggedInState == null || isOnboardingCompletedState == null) {
                // Return empty UI while loading from datastore (splash screen will persist)
                return@setContent
            }
            
            val startDestination = if (isLoggedInState == false) {
                Screen.Login.route
            } else if (isOnboardingCompletedState == false) {
                Screen.Onboarding.route
            } else {
                Screen.Dashboard.route
            }

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
                        val newTheme = if (isDark) Constants.THEME_LIGHT else Constants.THEME_DARK
                        CoroutineScope(Dispatchers.IO).launch {
                            dataStoreManager.setThemeMode(newTheme)
                        }
                    }
                )
            }
        }
    }
}