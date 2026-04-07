package com.example.tuf.presentation.screens.settings

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.example.tuf.core.base.BaseViewModel
import com.example.tuf.core.utils.Constants
import com.example.tuf.data.local.DataStoreManager
import com.example.tuf.domain.usecase.ExportTransactionsUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import java.io.File
import java.io.FileWriter

data class SettingsUiState(
    val themeMode: String = Constants.THEME_SYSTEM,
    val currencyCode: String = "INR",
    val currencySymbol: String = "₹",
    val dailyLimit: Double = 0.0,
    val weekStartDay: Int = 2,
    val accentColor: String = "#6C63FF",
    val backupReminderEnabled: Boolean = false,
    val isLoading: Boolean = false,
    val message: String? = null
)

sealed class SettingsEvent {
    data class ShowSnackbar(val message: String) : SettingsEvent()
    data class ShareCsv(val intent: Intent) : SettingsEvent()
    object ShowClearDataDialog : SettingsEvent()
}

class SettingsViewModel(
    private val dataStoreManager: DataStoreManager,
    private val exportTransactionsUseCase: ExportTransactionsUseCase
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private val _events = Channel<SettingsEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        observePreferences()
    }

    private fun observePreferences() {
        safeLaunch {
            combine(
                dataStoreManager.themeMode,
                dataStoreManager.currencyCode,
                dataStoreManager.currencySymbol,
                dataStoreManager.dailyLimit,
                dataStoreManager.weekStartDay
            ) { theme, code, symbol, limit, weekDay ->
                SettingsUiState(themeMode = theme, currencyCode = code, currencySymbol = symbol, dailyLimit = limit, weekStartDay = weekDay)
            }.collect { state ->
                _uiState.update { state }
            }
        }
    }

    fun setThemeMode(mode: String) {
        safeLaunch { dataStoreManager.setThemeMode(mode) }
    }

    fun setCurrency(code: String, symbol: String) {
        safeLaunch { dataStoreManager.setCurrency(code, symbol) }
    }

    fun setDailyLimit(limit: Double) {
        safeLaunch { dataStoreManager.setDailyLimit(limit) }
    }

    fun setWeekStartDay(day: Int) {
        safeLaunch { dataStoreManager.setWeekStartDay(day) }
    }

    fun setAccentColor(colorHex: String) {
        safeLaunch { dataStoreManager.setAccentColor(colorHex) }
    }

    fun exportToCsv(context: Context) {
        safeLaunch {
            _uiState.update { it.copy(isLoading = true) }
            val csv = exportTransactionsUseCase()
            val file = File(context.cacheDir, "finance_export.csv")
            FileWriter(file).use { it.write(csv) }
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/csv"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            _uiState.update { it.copy(isLoading = false) }
            _events.send(SettingsEvent.ShareCsv(Intent.createChooser(intent, "Export Transactions")))
        }
    }

    override fun onError(throwable: Throwable) {
        _uiState.update { it.copy(isLoading = false, message = throwable.message) }
    }
}
