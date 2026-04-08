package com.example.tuf.presentation.screens.transactions

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tuf.core.extensions.toRelativeDateLabel
import com.example.tuf.domain.model.Transaction
import com.example.tuf.domain.model.TransactionType
import com.example.tuf.presentation.components.*
import com.example.tuf.ui.theme.*
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

/**
 * Screen showing the filterable, searchable list of all transactions.
 * Supports swipe-to-delete with undo, swipe-to-edit, and grouped display by date.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionListScreen(
    viewModel: TransactionViewModel = koinViewModel(),
    onNavigateToAddTransaction: (String) -> Unit,
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    onNavigateToGroupSplit: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showFabMenu by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is TransactionEvent.ShowSnackbar -> {
                    val result = snackbarHostState.showSnackbar(
                        message = event.message,
                        actionLabel = event.actionLabel,
                        duration = SnackbarDuration.Short
                    )
                    if (result == SnackbarResult.ActionPerformed) event.onAction?.invoke()
                }
                is TransactionEvent.NavigateToEdit -> onNavigateToEdit(event.transactionId)
                else -> {}
            }
        }
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = {
                        if (!uiState.isSearchActive) {
                            Text("Transactions", fontWeight = FontWeight.Bold)
                        } else {
                            OutlinedTextField(
                                value = uiState.searchQuery,
                                onValueChange = { viewModel.onEvent(TransactionUiEvent.SearchQueryChanged(it)) },
                                placeholder = { Text("Search transactions...") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                shape = MaterialTheme.shapes.large,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            viewModel.onEvent(TransactionUiEvent.ToggleSearch(!uiState.isSearchActive))
                        }) {
                            Icon(
                                if (uiState.isSearchActive) Icons.Default.Close else Icons.Default.Search,
                                contentDescription = "Search"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )

                // Type filter tabs
                if (!uiState.isSearchActive) {
                    FilterTabs(
                        selectedType = uiState.selectedType,
                        onTypeSelected = { viewModel.onEvent(TransactionUiEvent.FilterByType(it)) }
                    )
                }
            }
        },
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.End) {
                AnimatedVisibility(visible = showFabMenu) {
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        ExtendedFloatingActionButton(
                            text = { Text("Add Income", color = Color.White) },
                            icon = { Icon(Icons.Default.TrendingUp, null, tint = Color.White) },
                            onClick = {
                                showFabMenu = false
                                onNavigateToAddTransaction(TransactionType.INCOME.name)
                            },
                            containerColor = Color(0xFF00C897)
                        )
                        ExtendedFloatingActionButton(
                            text = { Text("Add Expense", color = Color.White) },
                            icon = { Icon(Icons.Default.TrendingDown, null, tint = Color.White) },
                            onClick = {
                                showFabMenu = false
                                onNavigateToAddTransaction(TransactionType.EXPENSE.name)
                            },
                            containerColor = Color(0xFFFF4757)
                        )
                        ExtendedFloatingActionButton(
                            text = { Text("Group Expenses", color = Color.White) },
                            icon = { Icon(Icons.Default.Group, null, tint = Color.White) },
                            onClick = {
                                showFabMenu = false
                                onNavigateToGroupSplit()
                            },
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                FloatingActionButton(
                    onClick = { showFabMenu = !showFabMenu },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    AnimatedContent(targetState = showFabMenu, label = "fab_icon") { isOpen ->
                        Icon(
                            if (isOpen) Icons.Default.Close else Icons.Default.Add,
                            "Add transaction",
                            tint = Color.White
                        )
                    }
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        val groupedTransactions = remember(uiState.filteredTransactions) {
            uiState.filteredTransactions.groupBy { it.date.toRelativeDateLabel() }
        }

        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
                repeat(8) { TransactionItemShimmer() }
            }
            return@Scaffold
        }

        if (uiState.filteredTransactions.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                EmptyState(
                    title = if (uiState.isSearchActive) "No results found" else "No transactions",
                    subtitle = if (uiState.isSearchActive) "Try a different search term" else "Tap + to add your first transaction",
                    emoji = if (uiState.isSearchActive) "🔍" else "💸"
                )
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            contentPadding = PaddingValues(bottom = 96.dp)
        ) {
            groupedTransactions.forEach { (dateLabel, dayTransactions) ->
                item(key = "header_$dateLabel") {
                    DateSectionHeader(dateLabel = dateLabel)
                }
                staggeredItems(dayTransactions, key = { it.id }) { transaction, _ ->
                    SwipeableTransactionItem(
                        transaction = transaction,
                        currencySymbol = uiState.currencySymbol,
                        onDelete = { viewModel.onEvent(TransactionUiEvent.DeleteTransaction(transaction)) },
                        onEdit = { viewModel.onEvent(TransactionUiEvent.EditTransaction(transaction.id)) },
                        onClick = { onNavigateToDetail(transaction.id) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SwipeableTransactionItem(
    transaction: Transaction,
    currencySymbol: String,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    onClick: () -> Unit
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            when (value) {
                SwipeToDismissBoxValue.EndToStart -> { onDelete(); true }
                SwipeToDismissBoxValue.StartToEnd -> { onEdit(); false }
                else -> false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            val targetValue = dismissState.targetValue
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = Spacing.md, vertical = 2.dp)
                    .background(
                        when (targetValue) {
                            SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.error
                            SwipeToDismissBoxValue.StartToEnd -> Color(0xFF2196F3)
                            else -> Color.Transparent
                        },
                        MaterialTheme.shapes.medium
                    )
                    .padding(horizontal = 20.dp),
                horizontalArrangement = if (targetValue == SwipeToDismissBoxValue.EndToStart)
                    Arrangement.End else Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (targetValue == SwipeToDismissBoxValue.EndToStart)
                        Icons.Default.Delete else Icons.Default.Edit,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.md, vertical = 2.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(1.dp)
        ) {
            TransactionItem(
                transaction = transaction,
                currencySymbol = currencySymbol,
                onClick = onClick
            )
        }
    }
}

@Composable
private fun FilterTabs(
    selectedType: TransactionType?,
    onTypeSelected: (TransactionType?) -> Unit
) {
    val tabs = listOf(null to "All", TransactionType.INCOME to "Income", TransactionType.EXPENSE to "Expense")
    ScrollableTabRow(
        selectedTabIndex = tabs.indexOfFirst { it.first == selectedType }.coerceAtLeast(0),
        containerColor = MaterialTheme.colorScheme.background,
        edgePadding = Spacing.md
    ) {
        tabs.forEach { (type, label) ->
            Tab(
                selected = selectedType == type,
                onClick = { onTypeSelected(type) },
                text = {
                    Text(
                        label,
                        fontWeight = if (selectedType == type) FontWeight.Bold else FontWeight.Normal,
                        color = if (selectedType == type) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            )
        }
    }
}
