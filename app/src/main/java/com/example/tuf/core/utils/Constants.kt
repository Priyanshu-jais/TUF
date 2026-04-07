package com.example.tuf.core.utils

/** Application-wide constants for Finance Manager. */
object Constants {

    // ─── Transaction Types ──────────────────────────────────────────────────
    const val TYPE_INCOME = "INCOME"
    const val TYPE_EXPENSE = "EXPENSE"

    // ─── Recurring Frequencies ──────────────────────────────────────────────
    const val FREQUENCY_DAILY = "DAILY"
    const val FREQUENCY_WEEKLY = "WEEKLY"
    const val FREQUENCY_MONTHLY = "MONTHLY"
    const val FREQUENCY_YEARLY = "YEARLY"

    // ─── Category Types ─────────────────────────────────────────────────────
    const val CATEGORY_TYPE_INCOME = "INCOME"
    const val CATEGORY_TYPE_EXPENSE = "EXPENSE"
    const val CATEGORY_TYPE_BOTH = "BOTH"

    // ─── DataStore Keys ─────────────────────────────────────────────────────
    const val PREF_THEME_MODE = "theme_mode"
    const val PREF_ONBOARDING_COMPLETED = "onboarding_completed"
    const val PREF_DAILY_LIMIT = "daily_spending_limit"
    const val PREF_CURRENCY_CODE = "currency_code"
    const val PREF_CURRENCY_SYMBOL = "currency_symbol"
    const val PREF_WEEK_START_DAY = "week_start_day"
    const val PREF_ACCENT_COLOR = "accent_color"
    const val PREF_MONTH_START_DAY = "month_start_day"
    const val PREF_BACKUP_REMINDER = "backup_reminder"

    // ─── Theme Modes ─────────────────────────────────────────────────────────
    const val THEME_LIGHT = "LIGHT"
    const val THEME_DARK = "DARK"
    const val THEME_SYSTEM = "SYSTEM"

    // ─── Currency Options ────────────────────────────────────────────────────
    val CURRENCY_OPTIONS = listOf(
        Pair("INR", "₹"),
        Pair("USD", "$"),
        Pair("EUR", "€"),
        Pair("GBP", "£")
    )

    // ─── Pagination ──────────────────────────────────────────────────────────
    const val RECENT_TRANSACTIONS_LIMIT = 5

    // ─── Note Constraints ────────────────────────────────────────────────────
    const val MAX_NOTE_LENGTH = 150
    const val MAX_AMOUNT_DIGITS = 10

    // ─── Health Score Weights ─────────────────────────────────────────────────
    const val HEALTH_SCORE_SAVINGS_WEIGHT = 0.4f
    const val HEALTH_SCORE_BUDGET_WEIGHT = 0.4f
    const val HEALTH_SCORE_REGULARITY_WEIGHT = 0.2f

    // ─── Animation Durations ─────────────────────────────────────────────────
    const val ANIMATION_DURATION_SHORT = 200
    const val ANIMATION_DURATION_MEDIUM = 400
    const val ANIMATION_DURATION_LONG = 600

    // ─── Undo Delete Window ──────────────────────────────────────────────────
    const val UNDO_DELETE_DURATION_MS = 5000L

    // ─── Search Debounce ─────────────────────────────────────────────────────
    const val SEARCH_DEBOUNCE_MS = 300L
}
