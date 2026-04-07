package com.example.tuf.presentation.screens.transactions

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tuf.core.extensions.toFormattedDate
import com.example.tuf.core.extensions.toFormattedTime
import com.example.tuf.core.utils.CurrencyFormatter
import com.example.tuf.data.local.DataStoreManager
import com.example.tuf.domain.model.TransactionType
import com.example.tuf.presentation.components.ConfirmDialog
import com.example.tuf.presentation.components.EmptyState
import com.example.tuf.ui.theme.Spacing
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

/**
 * Detail view for a single transaction.
 * Currently retrieves the transaction detail via TransactionViewModel.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailScreen(
    transactionId: Long,
    viewModel: TransactionViewModel = koinViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val currencySymbol by koinInject<DataStoreManager>().currencySymbol.collectAsState(initial = "₹")
    val transaction = remember(uiState.transactions, transactionId) {
        uiState.transactions.find { it.id == transactionId }
    }
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog && transaction != null) {
        ConfirmDialog(
            title = "Delete Transaction",
            message = "Are you sure you want to delete this transaction?",
            confirmLabel = "Delete",
            isDestructive = true,
            onConfirm = {
                viewModel.onEvent(TransactionUiEvent.DeleteTransaction(transaction))
                showDeleteDialog = false
                onNavigateBack()
            },
            onDismiss = { showDeleteDialog = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transaction Detail", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    if (transaction != null) {
                        IconButton(onClick = { onNavigateToEdit(transactionId) }) {
                            Icon(Icons.Default.Edit, "Edit")
                        }
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, "Delete", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        if (transaction == null) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                EmptyState(title = "Transaction not found", emoji = "🔍")
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(Spacing.md),
            verticalArrangement = Arrangement.spacedBy(Spacing.md)
        ) {
            // Amount card
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (transaction.type == TransactionType.INCOME)
                        Color(0xFF00C897).copy(alpha = 0.1f)
                    else
                        MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
                ),
                shape = MaterialTheme.shapes.large
            ) {
                Box(modifier = Modifier.fillMaxWidth().padding(Spacing.lg), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "${if (transaction.type == TransactionType.INCOME) "+" else "-"}${CurrencyFormatter.format(transaction.amount, currencySymbol)}",
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Bold,
                            color = if (transaction.type == TransactionType.INCOME) Color(0xFF00C897) else MaterialTheme.colorScheme.error
                        )
                        Text(
                            transaction.type.name.lowercase().replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            // Details
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = MaterialTheme.shapes.large
            ) {
                Column(modifier = Modifier.padding(Spacing.md), verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
                    DetailRow("Category", transaction.category.name)
                    DetailRow("Date", transaction.date.toFormattedDate())
                    DetailRow("Time", transaction.date.toFormattedTime())
                    if (transaction.note.isNotBlank()) DetailRow("Note", transaction.note)
                    if (transaction.isRecurring) DetailRow("Type", "Recurring Transaction")
                    if (transaction.tags.isNotEmpty()) DetailRow("Tags", transaction.tags.joinToString(", ") { "#$it" })
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
    }
}
