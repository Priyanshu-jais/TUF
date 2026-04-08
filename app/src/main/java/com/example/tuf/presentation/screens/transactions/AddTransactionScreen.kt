package com.example.tuf.presentation.screens.transactions

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tuf.core.utils.Constants
import com.example.tuf.core.utils.DateUtils
import com.example.tuf.core.extensions.toFormattedDate
import com.example.tuf.data.local.DataStoreManager
import com.example.tuf.domain.model.*
import com.example.tuf.presentation.components.*
import com.example.tuf.ui.theme.*
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import kotlinx.coroutines.launch
import java.util.Calendar

/**
 * Full-screen Add/Edit Transaction screen.
 * Handles amount input, type toggle, category selection, date picker, note, and recurring toggle.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    viewModel: TransactionViewModel = koinViewModel(),
    initialType: String = Constants.TYPE_EXPENSE,
    transactionId: Long = -1L,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val dataStoreManager = koinInject<DataStoreManager>()
    val currencySymbol by dataStoreManager.currencySymbol.collectAsState(initial = "₹")

    var amount by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(TransactionType.fromString(initialType)) }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var note by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(DateUtils.now()) }
    var isRecurring by remember { mutableStateOf(false) }
    var frequency by remember { mutableStateOf(RecurringFrequency.MONTHLY) }
    var showDatePicker by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val addTransactionUseCase = koinInject<com.example.tuf.domain.usecase.AddTransactionUseCase>()
    val updateTransactionUseCase = koinInject<com.example.tuf.domain.usecase.UpdateTransactionUseCase>()
    val addRecurringUseCase = koinInject<com.example.tuf.domain.usecase.AddRecurringTransactionUseCase>()

    // Validation
    var amountError by remember { mutableStateOf<String?>(null) }
    var categoryError by remember { mutableStateOf<String?>(null) }
    var isSaving by remember { mutableStateOf(false) }
    var showSuccess by remember { mutableStateOf(false) }

    LaunchedEffect(showSuccess) {
        if (showSuccess) {
            kotlinx.coroutines.delay(800)
            onNavigateBack()
        }
    }

    LaunchedEffect(transactionId, uiState.transactions) {
        if (transactionId > 0 && amount.isEmpty()) {
            val tx = uiState.transactions.find { it.id == transactionId }
            if (tx != null) {
                amount = tx.amount.toString()
                selectedType = tx.type
                selectedCategory = tx.category
                note = tx.note
                selectedDate = tx.date
            }
        }
    }

    val filteredCategories = remember(uiState.categories, selectedType) {
        uiState.categories.filter { cat ->
            cat.type == CategoryType.fromString(selectedType.name) || cat.type == CategoryType.BOTH
        }
    }

    val gradientColors = if (selectedType == TransactionType.INCOME) {
        listOf(Color(0xFF00C897), Color(0xFF00A878))
    } else {
        listOf(Color(0xFFFF4757), Color(0xFFFF6B6B))
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDate)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { selectedDate = it }
                    showDatePicker = false
                }) { Text("OK") }
            }
        ) { DatePicker(state = datePickerState) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (transactionId > 0) "Edit Transaction" else "Add Transaction",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 32.dp)
                .imePadding()
        ) {
            // Amount display section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.linearGradient(gradientColors))
                    .padding(horizontal = Spacing.md, vertical = Spacing.xl),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "$currencySymbol${amount.ifBlank { "0" }}",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    // Type segmented control
                    Row(
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(50))
                            .padding(4.dp)
                    ) {
                        listOf(TransactionType.INCOME, TransactionType.EXPENSE).forEach { type ->
                            val isSelected = selectedType == type
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(if (isSelected) Color.White else Color.Transparent)
                                    .clickable { selectedType = type }
                                    .padding(horizontal = 20.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = type.name.lowercase().replaceFirstChar { it.uppercase() },
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) gradientColors.first() else Color.White
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.md))

            // Amount input
            OutlinedTextField(
                value = amount,
                onValueChange = { newVal ->
                    val filtered = newVal.filter { it.isDigit() || it == '.' }
                    if (filtered.length <= Constants.MAX_AMOUNT_DIGITS) {
                        amount = filtered
                        amountError = null
                    }
                },
                label = { Text("Amount") },
                leadingIcon = { Text(currencySymbol, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(start = 4.dp)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                isError = amountError != null,
                supportingText = amountError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.md),
                shape = MaterialTheme.shapes.medium
            )

            Spacer(modifier = Modifier.height(Spacing.md))

            // Category selector
            Text(
                "Category",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = Spacing.md, vertical = Spacing.xs)
            )
            if (categoryError != null) {
                Text(
                    categoryError!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = Spacing.md)
                )
            }
            LazyRow(
                contentPadding = PaddingValues(horizontal = Spacing.md),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                items(filteredCategories, key = { it.id }) { category ->
                    val isSelected = selectedCategory?.id == category.id
                    val scale by animateFloatAsState(
                        targetValue = if (isSelected) 1.1f else 1f,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy),
                        label = "category_scale"
                    )
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .graphicsLayer { scaleX = scale; scaleY = scale }
                            .clickable {
                                selectedCategory = category
                                categoryError = null
                            }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) category.color
                                    else category.color.copy(alpha = 0.15f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                getCategoryEmoji(category.iconName),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            category.name.take(8),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) category.color else MaterialTheme.colorScheme.onSurface.copy(0.7f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.md))

            // Date picker
            OutlinedCard(
                onClick = { showDatePicker = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.md),
                shape = MaterialTheme.shapes.medium
            ) {
                Row(
                    modifier = Modifier.padding(Spacing.md),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.md)
                ) {
                    Icon(Icons.Default.CalendarToday, null, tint = MaterialTheme.colorScheme.primary)
                    Text(selectedDate.toFormattedDate(), style = MaterialTheme.typography.bodyLarge)
                }
            }

            Spacer(modifier = Modifier.height(Spacing.md))

            // Note field
            OutlinedTextField(
                value = note,
                onValueChange = { if (it.length <= Constants.MAX_NOTE_LENGTH) note = it },
                label = { Text("Note (optional)") },
                placeholder = { Text("Add a note... Use #tags") },
                maxLines = 3,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.md),
                shape = MaterialTheme.shapes.medium,
                supportingText = {
                    Text("${note.length}/${Constants.MAX_NOTE_LENGTH}")
                }
            )

            Spacer(modifier = Modifier.height(Spacing.md))

            // Recurring toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.md),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Make Recurring?", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Medium)
                Switch(
                    checked = isRecurring,
                    onCheckedChange = { isRecurring = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colorScheme.primary)
                )
            }

            AnimatedVisibility(visible = isRecurring) {
                Column(modifier = Modifier.padding(horizontal = Spacing.md)) {
                    Text("Frequency", style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(vertical = Spacing.xs))
                    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
                        RecurringFrequency.entries.forEach { freq ->
                            FilterChip(
                                selected = frequency == freq,
                                onClick = { frequency = freq },
                                label = { Text(freq.displayName) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(Spacing.xl))

            // Save button
            AnimatedContent(
                targetState = showSuccess,
                label = "save_button",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Spacing.md)
            ) { success ->
                if (success) {
                    Button(
                        onClick = {},
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C897)),
                        shape = MaterialTheme.shapes.large
                    ) {
                        Icon(Icons.Default.CheckCircle, null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Saved!", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = {
                            // Validate
                            var valid = true
                            if (amount.isBlank() || amount.toDoubleOrNull() == null || amount.toDouble() <= 0) {
                                amountError = "Please enter a valid amount"
                                valid = false
                            }
                            if (selectedCategory == null) {
                                categoryError = "Please select a category"
                                valid = false
                            }
                            if (valid) {
                                isSaving = true
                                scope.launch {
                                    try {
                                        if (transactionId > 0) {
                                            val updatedTransaction = Transaction(
                                                id = transactionId,
                                                amount = amount.toDouble(),
                                                type = selectedType,
                                                category = selectedCategory!!,
                                                note = note,
                                                date = selectedDate,
                                                createdAt = DateUtils.now() // Or keep old
                                            )
                                            updateTransactionUseCase(updatedTransaction)
                                        } else {
                                            val transaction = Transaction(
                                                amount = amount.toDouble(),
                                                type = selectedType,
                                                category = selectedCategory!!,
                                                note = note,
                                                date = selectedDate,
                                                createdAt = DateUtils.now()
                                            )
                                            addTransactionUseCase(transaction)
                                            if (isRecurring) {
                                                val recurring = RecurringTransaction(
                                                    amount = transaction.amount,
                                                    type = selectedType,
                                                    category = selectedCategory!!,
                                                    frequency = frequency,
                                                    startDate = selectedDate,
                                                    nextDueDate = selectedDate,
                                                    note = note
                                                )
                                                addRecurringUseCase(recurring)
                                            }
                                        }
                                        showSuccess = true
                                    } catch (e: Exception) {
                                        isSaving = false
                                        amountError = "Save failed: ${e.message}"
                                    }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = gradientColors.first()
                        ),
                        shape = MaterialTheme.shapes.large,
                        enabled = !isSaving
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                "Save Transaction",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
