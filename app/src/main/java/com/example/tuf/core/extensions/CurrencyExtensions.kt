package com.example.tuf.core.extensions

import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

/** Formats a Double as a currency string with the given [currencyCode] (e.g. "INR", "USD"). */
fun Double.toCurrencyString(currencyCode: String = "INR"): String {
    return try {
        val format = NumberFormat.getCurrencyInstance(Locale.getDefault())
        format.currency = Currency.getInstance(currencyCode)
        format.maximumFractionDigits = 2
        format.minimumFractionDigits = 0
        format.format(this)
    } catch (e: Exception) {
        "₹${this.toFormattedAmount()}"
    }
}

/** Formats a Double as a currency string with an explicit symbol prefix. */
fun Double.toCurrencyStringWithSymbol(symbol: String = "₹"): String {
    return "$symbol${this.toFormattedAmount()}"
}

/** Formats the amount with commas and 2 decimal places when needed. */
fun Double.toFormattedAmount(): String {
    return if (this % 1 == 0.0) {
        "%,.0f".format(this)
    } else {
        "%,.2f".format(this)
    }
}

/** Returns + or - prefix based on sign. */
fun Double.toSignedString(symbol: String = "₹"): String {
    return if (this >= 0) "+$symbol${this.toFormattedAmount()}"
    else "-$symbol${Math.abs(this).toFormattedAmount()}"
}

/** Formats as percentage string with one decimal (e.g. "73.5%"). */
fun Double.toPercentageString(): String = "%.1f%%".format(this)

/** Returns the absolute value of a Double. */
fun Double.absoluteValue(): Double = Math.abs(this)
