package com.example.tuf.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import com.example.tuf.core.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "finance_prefs")

/**
 * Manages all persistent user preferences using Jetpack DataStore.
 *
 * Provides reactive [Flow]-based access and suspend-based write functions
 * for theme, onboarding, currency, and other app settings.
 */
class DataStoreManager(private val context: Context) {

    // ─── Keys ─────────────────────────────────────────────────────────────────
    private object Keys {
        val THEME_MODE = stringPreferencesKey(Constants.PREF_THEME_MODE)
        val ONBOARDING_COMPLETED = booleanPreferencesKey(Constants.PREF_ONBOARDING_COMPLETED)
        val DAILY_LIMIT = doublePreferencesKey(Constants.PREF_DAILY_LIMIT)
        val CURRENCY_CODE = stringPreferencesKey(Constants.PREF_CURRENCY_CODE)
        val CURRENCY_SYMBOL = stringPreferencesKey(Constants.PREF_CURRENCY_SYMBOL)
        val WEEK_START_DAY = intPreferencesKey(Constants.PREF_WEEK_START_DAY)
        val ACCENT_COLOR = stringPreferencesKey(Constants.PREF_ACCENT_COLOR)
        val MONTH_START_DAY = intPreferencesKey(Constants.PREF_MONTH_START_DAY)
        val BACKUP_REMINDER = booleanPreferencesKey(Constants.PREF_BACKUP_REMINDER)
    }

    private val dataStore = context.dataStore

    private fun <T> Flow<Preferences>.safeMap(default: T, transform: (Preferences) -> T): Flow<T> =
        catch { e -> if (e is IOException) emit(emptyPreferences()) else throw e }
            .map { prefs -> transform(prefs) }

    // ─── Theme ────────────────────────────────────────────────────────────────
    val themeMode: Flow<String> = dataStore.data.safeMap(Constants.THEME_SYSTEM) { prefs ->
        prefs[Keys.THEME_MODE] ?: Constants.THEME_SYSTEM
    }

    suspend fun setThemeMode(mode: String) {
        dataStore.edit { it[Keys.THEME_MODE] = mode }
    }

    // ─── Onboarding ───────────────────────────────────────────────────────────
    val isOnboardingCompleted: Flow<Boolean> = dataStore.data.safeMap(false) { prefs ->
        prefs[Keys.ONBOARDING_COMPLETED] ?: false
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        dataStore.edit { it[Keys.ONBOARDING_COMPLETED] = completed }
    }

    // ─── Daily Limit ──────────────────────────────────────────────────────────
    val dailyLimit: Flow<Double> = dataStore.data.safeMap(0.0) { prefs ->
        prefs[Keys.DAILY_LIMIT] ?: 0.0
    }

    suspend fun setDailyLimit(limit: Double) {
        dataStore.edit { it[Keys.DAILY_LIMIT] = limit }
    }

    // ─── Currency ─────────────────────────────────────────────────────────────
    val currencyCode: Flow<String> = dataStore.data.safeMap("INR") { prefs ->
        prefs[Keys.CURRENCY_CODE] ?: "INR"
    }

    val currencySymbol: Flow<String> = dataStore.data.safeMap("₹") { prefs ->
        prefs[Keys.CURRENCY_SYMBOL] ?: "₹"
    }

    suspend fun setCurrency(code: String, symbol: String) {
        dataStore.edit {
            it[Keys.CURRENCY_CODE] = code
            it[Keys.CURRENCY_SYMBOL] = symbol
        }
    }

    // ─── Week Start Day ───────────────────────────────────────────────────────
    val weekStartDay: Flow<Int> = dataStore.data.safeMap(2) { prefs ->
        prefs[Keys.WEEK_START_DAY] ?: 2 // 2 = Monday (Calendar.MONDAY)
    }

    suspend fun setWeekStartDay(day: Int) {
        dataStore.edit { it[Keys.WEEK_START_DAY] = day }
    }

    // ─── Accent Color ─────────────────────────────────────────────────────────
    val accentColor: Flow<String> = dataStore.data.safeMap("#6C63FF") { prefs ->
        prefs[Keys.ACCENT_COLOR] ?: "#6C63FF"
    }

    suspend fun setAccentColor(colorHex: String) {
        dataStore.edit { it[Keys.ACCENT_COLOR] = colorHex }
    }

    // ─── Backup Reminder ──────────────────────────────────────────────────────
    val backupReminderEnabled: Flow<Boolean> = dataStore.data.safeMap(false) { prefs ->
        prefs[Keys.BACKUP_REMINDER] ?: false
    }

    suspend fun setBackupReminderEnabled(enabled: Boolean) {
        dataStore.edit { it[Keys.BACKUP_REMINDER] = enabled }
    }
}
