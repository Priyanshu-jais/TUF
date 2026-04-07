package com.example.tuf.presentation.screens.recurring

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tuf.core.extensions.toFormattedDate
import com.example.tuf.core.utils.CurrencyFormatter
import com.example.tuf.data.local.DataStoreManager
import com.example.tuf.domain.model.RecurringTransaction
import com.example.tuf.domain.model.TransactionType
import com.example.tuf.presentation.components.EmptyState
import com.example.tuf.presentation.components.SectionHeader
import com.example.tuf.presentation.components.getCategoryEmoji
import com.example.tuf.ui.theme.Spacing
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

/**
 * Recurring transactions management screen.
 * Shows active/paused recurring rules with toggle and delete actions.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecurringScreen(
    viewModel: RecurringViewModel = koinViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToAddRecurring: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val currencySymbol by koinInject<DataStoreManager>().currencySymbol.collectAsState(initial = "₹")
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recurring", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddRecurring,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, "Add recurring", tint = Color.White)
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        if (uiState.recurringTransactions.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                EmptyState(
                    title = "No recurring transactions",
                    subtitle = "Set up automatic recurring rules for regular income or expenses",
                    emoji = "🔄",
                    actionLabel = "Add Recurring",
                    onAction = onNavigateToAddRecurring
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentPadding = PaddingValues(bottom = 96.dp)
            ) {
                if (uiState.dueToday.isNotEmpty()) {
                    item { SectionHeader(title = "⚡ Due Today") }
                    items(uiState.dueToday, key = { "due_${it.id}" }) { recurring ->
                        RecurringCard(
                            recurring = recurring,
                            currencySymbol = currencySymbol,
                            isDue = true,
                            onToggle = { viewModel.toggleActive(recurring) },
                            onDelete = { viewModel.delete(recurring) }
                        )
                    }
                }
                item { SectionHeader(title = "All Recurring", modifier = Modifier.padding(top = Spacing.md)) }
                items(uiState.recurringTransactions, key = { it.id }) { recurring ->
                    RecurringCard(
                        recurring = recurring,
                        currencySymbol = currencySymbol,
                        isDue = false,
                        onToggle = { viewModel.toggleActive(recurring) },
                        onDelete = { viewModel.delete(recurring) }
                    )
                }
            }
        }
    }
}

@Composable
private fun RecurringCard(
    recurring: RecurringTransaction,
    currencySymbol: String,
    isDue: Boolean,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }
    if (showDeleteConfirm) {
        com.example.tuf.presentation.components.ConfirmDialog(
            title = "Delete Recurring Rule",
            message = "This will delete the recurring rule. Past transactions will be unaffected.",
            confirmLabel = "Delete",
            isDestructive = true,
            onConfirm = { onDelete(); showDeleteConfirm = false },
            onDismiss = { showDeleteConfirm = false }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Spacing.md, vertical = Spacing.xs),
        colors = CardDefaults.cardColors(
            containerColor = if (isDue) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(1.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(Spacing.md),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category Icon
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(recurring.category.color.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                getCategoryEmoji(recurring.category.iconName).let { emoji ->
                    Text(emoji, style = MaterialTheme.typography.titleMedium)
                }
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    recurring.category.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "${recurring.frequency.displayName} • Next: ${recurring.nextDueDate.toFormattedDate()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "${if (recurring.type == TransactionType.INCOME) "+" else "-"}${CurrencyFormatter.format(recurring.amount, currencySymbol)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (recurring.type == TransactionType.INCOME) Color(0xFF00C897) else MaterialTheme.colorScheme.error
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { showDeleteConfirm = true }, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                    }
                    Switch(
                        checked = recurring.isActive,
                        onCheckedChange = { onToggle() },
                        modifier = Modifier.scale(0.8f)
                    )
                }
            }
        }
    }
}
