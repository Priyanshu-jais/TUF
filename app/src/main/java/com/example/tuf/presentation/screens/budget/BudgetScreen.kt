package com.example.tuf.presentation.screens.budget

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tuf.core.utils.CurrencyFormatter
import com.example.tuf.domain.model.Category
import com.example.tuf.presentation.components.*
import com.example.tuf.ui.theme.Spacing
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

/**
 * Budget management screen with overall donut ring and per-category progress cards.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetScreen(
    viewModel: BudgetViewModel = koinViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var showAddBudgetSheet by remember { mutableStateOf(false) }
    var budgetToEdit by remember { mutableStateOf<com.example.tuf.domain.model.Budget?>(null) }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is BudgetEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    if (showAddBudgetSheet) {
        AddBudgetBottomSheet(
            categories = uiState.expenseCategories,
            currencySymbol = uiState.currencySymbol,
            budgetToEdit = budgetToEdit,
            onSave = { categoryId, limit ->
                viewModel.addOrUpdateBudget(categoryId, limit)
                showAddBudgetSheet = false
                budgetToEdit = null
            },
            onDelete = {
                budgetToEdit?.let { viewModel.deleteBudget(it) }
                showAddBudgetSheet = false
                budgetToEdit = null
            },
            onDismiss = { 
                showAddBudgetSheet = false 
                budgetToEdit = null
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Budget", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { 
                    budgetToEdit = null
                    showAddBudgetSheet = true 
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, "Add budget", tint = Color.White)
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 80.dp)
        ) {
            MonthNavigator(
                month = uiState.selectedMonth,
                year = uiState.selectedYear,
                onPrevious = { viewModel.previousMonth() },
                onNext = { viewModel.nextMonth() },
                modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.md)
            )

            // Overall budget donut ring
            val totalLimit = uiState.budgetProgress.sumOf { it.budget.limitAmount }
            val totalSpent = uiState.budgetProgress.sumOf { it.spent }
            val overallPct = if (totalLimit > 0) (totalSpent / totalLimit).toFloat() else 0f

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                BudgetDonutRing(
                    spent = totalSpent,
                    limit = totalLimit,
                    percentage = overallPct,
                    currencySymbol = uiState.currencySymbol,
                    modifier = Modifier.padding(Spacing.lg)
                )
            }

            SectionHeader(
                title = "Category Budgets",
                modifier = Modifier.padding(top = Spacing.md)
            )

            if (uiState.budgetProgress.isEmpty()) {
                EmptyState(
                    title = "No budgets set",
                    subtitle = "Tap + to set your first monthly budget",
                    emoji = "💰",
                    actionLabel = "Set Budget",
                    onAction = { showAddBudgetSheet = true; budgetToEdit = null }
                )
            } else {
                Column(
                    modifier = Modifier.padding(horizontal = Spacing.md),
                    verticalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    uiState.budgetProgress.forEach { progress ->
                        BudgetProgressCard(
                            progress = progress,
                            currencySymbol = uiState.currencySymbol,
                            onClick = { 
                                budgetToEdit = progress.budget
                                showAddBudgetSheet = true 
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BudgetDonutRing(
    spent: Double,
    limit: Double,
    percentage: Float,
    currencySymbol: String,
    modifier: Modifier = Modifier
) {
    val animatedSweep by animateFloatAsState(
        targetValue = (percentage.coerceIn(0f, 1f)) * 270f,
        animationSpec = tween(1000),
        label = "donut_sweep"
    )
    val ringColor = when {
        percentage >= 1f -> MaterialTheme.colorScheme.error
        percentage >= 0.8f -> Color(0xFFFFC107)
        else -> Color(0xFF00C897)
    }

    Box(modifier = modifier.size(200.dp), contentAlignment = Alignment.Center) {
        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeW = 24.dp.toPx()
            val arcSize = Size(size.width - strokeW, size.height - strokeW)
            val arcOffset = Offset(strokeW / 2, strokeW / 2)
            drawArc(ringColor.copy(0.15f), 135f, 270f, false, arcOffset, arcSize, style = Stroke(strokeW, cap = StrokeCap.Round))
            drawArc(ringColor, 135f, animatedSweep, false, arcOffset, arcSize, style = Stroke(strokeW, cap = StrokeCap.Round))
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(CurrencyFormatter.format(spent, currencySymbol), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text("of ${CurrencyFormatter.format(limit, currencySymbol)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(0.6f))
            Text("${(percentage * 100).toInt()}% used", style = MaterialTheme.typography.labelMedium, color = ringColor, fontWeight = FontWeight.SemiBold)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddBudgetBottomSheet(
    categories: List<Category>,
    currencySymbol: String,
    budgetToEdit: com.example.tuf.domain.model.Budget?,
    onSave: (Long, Double) -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit
) {
    var selectedCategory by remember(budgetToEdit) { mutableStateOf(budgetToEdit?.category) }
    var limitText by remember(budgetToEdit) { mutableStateOf(budgetToEdit?.limitAmount?.toString() ?: "") }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(modifier = Modifier.padding(Spacing.md).padding(bottom = 32.dp)) {
            Text("Set Budget", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(Spacing.md))

            // Category dropdown
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                OutlinedTextField(
                    value = selectedCategory?.name ?: "Select Category",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    categories.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat.name) },
                            onClick = { selectedCategory = cat; expanded = false }
                        )
                    }
                }
            }

            Spacer(Modifier.height(Spacing.md))
            OutlinedTextField(
                value = limitText,
                onValueChange = { limitText = it.filter { c -> c.isDigit() || c == '.' } },
                label = { Text("Monthly Limit ($currencySymbol)") },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal)
            )

            Spacer(Modifier.height(Spacing.lg))
            Button(
                onClick = {
                    val cat = selectedCategory ?: return@Button
                    val limit = limitText.toDoubleOrNull() ?: return@Button
                    onSave(cat.id, limit)
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = MaterialTheme.shapes.large
            ) {
                Text(if (budgetToEdit != null) "Update Budget" else "Save Budget", fontWeight = FontWeight.Bold)
            }
            
            if (budgetToEdit != null) {
                Spacer(Modifier.height(Spacing.sm))
                OutlinedButton(
                    onClick = onDelete,
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Remove Budget", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
