package com.example.tuf.presentation.screens.settings

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tuf.core.utils.Constants
import com.example.tuf.presentation.components.ConfirmDialog
import com.example.tuf.ui.theme.Spacing
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

/**
 * Settings screen for app preferences: theme, currency, daily limit, export.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = koinViewModel(),
    onNavigateBack: () -> Unit,
    onThemeToggle: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is SettingsEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
                is SettingsEvent.ShareCsv -> context.startActivity(event.intent)
                else -> {}
            }
        }
    }

    var showDailyLimitDialog by remember { mutableStateOf(false) }
    var dailyLimitInput by remember { mutableStateOf("") }

    if (showDailyLimitDialog) {
        AlertDialog(
            onDismissRequest = { showDailyLimitDialog = false },
            title = { Text("Set Daily Limit") },
            text = {
                OutlinedTextField(
                    value = dailyLimitInput,
                    onValueChange = { dailyLimitInput = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Amount (${uiState.currencySymbol})") },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal
                    )
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.setDailyLimit(dailyLimitInput.toDoubleOrNull() ?: 0.0)
                    showDailyLimitDialog = false
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showDailyLimitDialog = false }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // Appearance
            item { SettingsSectionHeader("Appearance") }
            item {
                SettingsToggleRow(
                    icon = Icons.Default.DarkMode,
                    title = "Dark Mode",
                    subtitle = "Switch between light & dark",
                    checked = uiState.themeMode == Constants.THEME_DARK,
                    onCheckedChange = {
                        onThemeToggle()
                        viewModel.setThemeMode(if (it) Constants.THEME_DARK else Constants.THEME_LIGHT)
                    }
                )
            }

            // Finance
            item { SettingsSectionHeader("Finance") }
            item {
                SettingsClickRow(
                    icon = Icons.Default.CurrencyRupee,
                    title = "Currency",
                    subtitle = "${uiState.currencyCode} (${uiState.currencySymbol})",
                    onClick = { /* TODO: currency picker */ }
                )
            }
            item {
                SettingsClickRow(
                    icon = Icons.Default.MoneyOff,
                    title = "Daily Spending Limit",
                    subtitle = if (uiState.dailyLimit > 0) "${uiState.currencySymbol}${uiState.dailyLimit}" else "Not set",
                    onClick = {
                        dailyLimitInput = if (uiState.dailyLimit > 0) uiState.dailyLimit.toString() else ""
                        showDailyLimitDialog = true
                    }
                )
            }

            // Data
            item { SettingsSectionHeader("Data") }
            item {
                SettingsClickRow(
                    icon = Icons.Default.FileDownload,
                    title = "Export to CSV",
                    subtitle = "Share all transactions as a CSV file",
                    onClick = { viewModel.exportToCsv(context) }
                )
            }

            // About
            item { SettingsSectionHeader("About") }
            item {
                SettingsInfoRow(icon = Icons.Default.Info, title = "Version", value = "1.0.0")
            }
        }
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        title,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = Spacing.md, top = Spacing.lg, bottom = Spacing.xs)
    )
}

@Composable
private fun SettingsToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    ListItem(
        headlineContent = { Text(title, fontWeight = FontWeight.Medium) },
        supportingContent = { Text(subtitle, style = MaterialTheme.typography.bodySmall) },
        leadingContent = { Icon(icon, null, tint = MaterialTheme.colorScheme.primary) },
        trailingContent = { Switch(checked = checked, onCheckedChange = onCheckedChange) }
    )
    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
}

@Composable
private fun SettingsClickRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = { Text(title, fontWeight = FontWeight.Medium) },
        supportingContent = { Text(subtitle, style = MaterialTheme.typography.bodySmall) },
        leadingContent = { Icon(icon, null, tint = MaterialTheme.colorScheme.primary) },
        trailingContent = { Icon(Icons.Default.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurface.copy(0.4f)) },
        modifier = Modifier.clickable { onClick() }
    )
    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
}

@Composable
private fun SettingsInfoRow(
    icon: ImageVector,
    title: String,
    value: String
) {
    ListItem(
        headlineContent = { Text(title, fontWeight = FontWeight.Medium) },
        leadingContent = { Icon(icon, null, tint = MaterialTheme.colorScheme.primary) },
        trailingContent = {
            Text(value, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(0.6f))
        }
    )
}
