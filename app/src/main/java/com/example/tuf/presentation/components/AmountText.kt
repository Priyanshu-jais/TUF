package com.example.tuf.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.example.tuf.domain.model.TransactionType
import com.example.tuf.ui.theme.ExpenseLight
import com.example.tuf.ui.theme.IncomeLight

/**
 * Displays a colored currency amount with sign prefix.
 *
 * @param amount The amount (always positive).
 * @param type Whether this is income or expense — controls color and sign.
 * @param currencySymbol Symbol prefix.
 * @param style Text style.
 * @param modifier Layout modifier.
 */
@Composable
fun AmountText(
    amount: Double,
    type: TransactionType,
    currencySymbol: String = "₹",
    style: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.titleMedium,
    modifier: Modifier = Modifier
) {
    val color = if (type == TransactionType.INCOME) {
        MaterialTheme.colorScheme.tertiary
    } else {
        MaterialTheme.colorScheme.error
    }
    val prefix = if (type == TransactionType.INCOME) "+" else "-"
    val formattedAmount = if (amount % 1 == 0.0) "%,.0f".format(amount) else "%,.2f".format(amount)

    Text(
        text = "$prefix$currencySymbol$formattedAmount",
        style = style,
        fontWeight = FontWeight.Bold,
        color = color,
        modifier = modifier
    )
}
