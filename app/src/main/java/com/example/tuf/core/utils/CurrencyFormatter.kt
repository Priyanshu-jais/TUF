package com.example.tuf.core.utils

/**
 * Formats currency amounts for display throughout the app.
 */
object CurrencyFormatter {

    /**
     * Formats [amount] with the given [symbol] (e.g. "₹") as a display string.
     * - Integer amounts show no decimal: "₹1,500"
     * - Decimal amounts show 2 places: "₹1,500.50"
     */
    fun format(amount: Double, symbol: String = "₹"): String {
        return if (amount % 1 == 0.0) {
            "$symbol${"%,.0f".format(amount)}"
        } else {
            "$symbol${"%,.2f".format(amount)}"
        }
    }

    /**
     * Returns the currency symbol for a given ISO [currencyCode].
     */
    fun symbolFor(currencyCode: String): String = when (currencyCode) {
        "INR" -> "₹"
        "USD" -> "$"
        "EUR" -> "€"
        "GBP" -> "£"
        else -> currencyCode
    }

    /**
     * Parses a raw amount input string (which may contain commas) into a [Double].
     * Returns 0.0 if parsing fails.
     */
    fun parseAmount(input: String): Double {
        return input.replace(",", "").toDoubleOrNull() ?: 0.0
    }
}
