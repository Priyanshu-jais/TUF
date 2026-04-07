package com.example.tuf.presentation.screens.search

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tuf.data.local.DataStoreManager
import com.example.tuf.presentation.components.*
import com.example.tuf.ui.theme.Spacing
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

/**
 * Global search screen with debounced search input and categorized results.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel = koinViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val currencySymbol by koinInject<DataStoreManager>().currencySymbol.collectAsState(initial = "₹")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    TextField(
                        value = uiState.query,
                        onValueChange = { viewModel.onQueryChanged(it) },
                        placeholder = { Text("Search by note, category, tags...") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.background,
                            unfocusedContainerColor = MaterialTheme.colorScheme.background,
                            focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, "Back") }
                },
                actions = {
                    if (uiState.query.isNotBlank()) {
                        IconButton(onClick = { viewModel.onQueryChanged("") }) {
                            Icon(Icons.Default.Close, "Clear")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.query.isBlank() -> {
                    EmptyState(
                        title = "Search Transactions",
                        subtitle = "Search by note, category name, or #tags",
                        emoji = "🔍",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                uiState.hasSearched && uiState.results.isEmpty() -> {
                    EmptyState(
                        title = "No results",
                        subtitle = "Try a different search term",
                        emoji = "📭",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                else -> {
                    LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 32.dp)) {
                        if (uiState.results.isNotEmpty()) {
                            item {
                                Text(
                                    "${uiState.results.size} results",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onSurface.copy(0.6f),
                                    modifier = Modifier.padding(Spacing.md)
                                )
                            }
                            staggeredItems(uiState.results, key = { it.id }) { transaction, _ ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = Spacing.md, vertical = 2.dp),
                                    onClick = { onNavigateToDetail(transaction.id) },
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    elevation = CardDefaults.cardElevation(1.dp)
                                ) {
                                    TransactionItem(
                                        transaction = transaction,
                                        currencySymbol = currencySymbol
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
