package com.example.tuf.presentation.screens.dashboard

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.unit.dp
import com.example.tuf.core.extensions.toRelativeDateLabel
import com.example.tuf.domain.model.TransactionType
import com.example.tuf.presentation.components.*
import com.example.tuf.presentation.components.getCategoryEmoji
import com.example.tuf.ui.theme.*
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import com.example.tuf.data.local.DataStoreManager
import kotlinx.coroutines.launch

/**
 * Dashboard / Home screen showing the monthly financial overview,
 * quick actions, recent transactions, budgets, and insights.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = koinViewModel(),
    onNavigateToAddTransaction: (String) -> Unit,
    onNavigateToTransactions: () -> Unit,
    onNavigateToAllBudgets: () -> Unit,
    onNavigateToAnalytics: () -> Unit,
    onNavigateToDetail: (Long) -> Unit,
    onThemeToggle: () -> Unit,
    onMenuClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val selectedMonth by viewModel.selectedMonth.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    
    val dataStoreManager: DataStoreManager = koinInject()
    val isFtuxCompleted by dataStoreManager.isFtuxCompleted.collectAsState(initial = true)
    val userName by dataStoreManager.userName.collectAsState(initial = "User")
    val profilePicUri by dataStoreManager.profilePicUri.collectAsState(initial = null)
    
    var tooltipStep by remember(isFtuxCompleted) { mutableStateOf(if (isFtuxCompleted) 0 else 1) }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is DashboardEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
                is DashboardEvent.NavigateToAddTransaction -> onNavigateToAddTransaction(event.type)
                is DashboardEvent.NavigateToTransactions -> onNavigateToTransactions()
                else -> {}
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        if (uiState.isLoading) {
            DashboardShimmer()
            return@Scaffold
        }
        
        Box(modifier = Modifier.fillMaxSize()) {

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            // Top bar
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.md, vertical = Spacing.sm),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // LEFT: Menu Icon + Greeting
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onMenuClick) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = MaterialTheme.colorScheme.primary)
                        }
                        Column {
                            Text(
                                "Good ${getGreeting()}, $userName 👋",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                            Text(
                                "Finance Manager",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    
                    // RIGHT: Theme toggle & Profile Avatar
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onThemeToggle) {
                            Icon(Icons.Default.DarkMode, "Toggle theme", tint = MaterialTheme.colorScheme.primary)
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            if (profilePicUri != null) {
                                AsyncImage(
                                    model = profilePicUri,
                                    contentDescription = "Profile Picture",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Text(
                                    text = userName.take(1).uppercase(),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Month Navigator
            item {
                MonthNavigator(
                    month = selectedMonth.first,
                    year = selectedMonth.second,
                    onPrevious = { viewModel.previousMonth() },
                    onNext = { viewModel.nextMonth() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.md)
                )
            }

            // Balance Card
            item {
                BalanceCard(
                    summary = uiState.summary,
                    isBalanceVisible = uiState.isBalanceVisible,
                    onToggleVisibility = { viewModel.onEvent(DashboardUiEvent.ToggleBalanceVisibility) },
                    currencySymbol = uiState.currencySymbol,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.md, vertical = Spacing.sm)
                )
            }

            // Quick Actions
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Spacing.md, vertical = Spacing.md),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    QuickActionButton(
                        icon = Icons.Default.Add,
                        label = "Add Income",
                        gradientColors = listOf(Color(0xFF00C897), Color(0xFF00A878)),
                        onClick = { onNavigateToAddTransaction(TransactionType.INCOME.name) }
                    )
                    QuickActionButton(
                        icon = Icons.Default.Remove,
                        label = "Add Expense",
                        gradientColors = listOf(Color(0xFFFF4757), Color(0xFFFF6B6B)),
                        onClick = { onNavigateToAddTransaction(TransactionType.EXPENSE.name) }
                    )
                    QuickActionButton(
                        icon = Icons.Default.BarChart,
                        label = "Analytics",
                        gradientColors = listOf(Color(0xFF6C63FF), Color(0xFF9B8FFF)),
                        onClick = onNavigateToAnalytics
                    )
                    QuickActionButton(
                        icon = Icons.Default.PieChart,
                        label = "Budget",
                        gradientColors = listOf(Color(0xFFFF6584), Color(0xFFFF8FA3)),
                        onClick = onNavigateToAllBudgets
                    )
                }
            }

            // Financial Health Score + Insights
            if (uiState.healthScore != null) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Spacing.md, vertical = Spacing.sm),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(Spacing.md),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(Spacing.lg)
                        ) {
                            HealthScoreRing(
                                score = uiState.healthScore,
                                ringSize = 120f,
                                strokeWidth = 16f
                            )
                            Column {
                                Text(
                                    "Financial Health",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "Based on savings, budget\nadherence & regularity",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                }
            }

            // Insights
            if (uiState.insights.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "💡 Insights",
                        modifier = Modifier.padding(top = Spacing.sm)
                    )
                }
                items(uiState.insights) { insight ->
                    InsightCard(
                        insight = insight,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Spacing.md, vertical = Spacing.xs)
                    )
                }
            }

            // Recent Transactions
            item {
                SectionHeader(
                    title = "Recent Transactions",
                    actionLabel = "See All",
                    onAction = onNavigateToTransactions,
                    modifier = Modifier.padding(top = Spacing.md)
                )
            }

            if (uiState.recentTransactions.isEmpty()) {
                item {
                    EmptyState(
                        title = "No transactions yet",
                        subtitle = "Add your first income or expense",
                        emoji = "💸",
                        actionLabel = "Add Transaction",
                        onAction = { onNavigateToAddTransaction(TransactionType.EXPENSE.name) }
                    )
                }
            } else {
                staggeredItems(uiState.recentTransactions, key = { it.id }) { transaction, _ ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Spacing.md, vertical = 2.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(1.dp)
                    ) {
                        TransactionItem(
                            transaction = transaction,
                            currencySymbol = uiState.currencySymbol,
                            onClick = { onNavigateToDetail(transaction.id) }
                        )
                    }
                }
            }

            // Budget Overview
            if (uiState.budgetProgress.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "Budget Overview",
                        actionLabel = "Manage",
                        onAction = onNavigateToAllBudgets,
                        modifier = Modifier.padding(top = Spacing.md)
                    )
                }
                item {
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = Spacing.md),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                    ) {
                        items(uiState.budgetProgress.take(5), key = { it.budget.id }) { progress ->
                            BudgetProgressCard(
                                progress = progress,
                                currencySymbol = uiState.currencySymbol,
                                modifier = Modifier.width(220.dp)
                            )
                        }
                    }
                }
            }
        }
        
        // Render Multi-step FTUX Toolkit
        if (tooltipStep == 1) {
            Tooltip(
                text = "Welcome to TUF! 🎉\nTap the hamburger menu on the top left to open your sidebar.",
                isVisible = true,
                onDismiss = { tooltipStep = 2 },
                modifier = Modifier.align(Alignment.TopStart).padding(top = 80.dp, start = 16.dp)
            )
        } else if (tooltipStep == 2) {
            Tooltip(
                text = "Manage everything easily.\nTap the Add Income or Expense buttons to log money.",
                isVisible = true,
                onDismiss = { tooltipStep = 3 },
                modifier = Modifier.align(Alignment.Center)
            )
        } else if (tooltipStep == 3) {
            Tooltip(
                text = "Set Budgets per category and \nget notified if you overspend! Let's get started.",
                isVisible = true,
                onDismiss = {
                    tooltipStep = 0
                    scope.launch { dataStoreManager.setFtuxCompleted(true) }
                },
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 120.dp)
            )
        }
        }
    }
}

private fun getGreeting(): String {
    val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
    return when {
        hour < 12 -> "Morning"
        hour < 17 -> "Afternoon"
        else -> "Evening"
    }
}
