package com.example.tuf.presentation.screens.categories

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tuf.domain.model.Category
import com.example.tuf.domain.model.CategoryType
import com.example.tuf.presentation.components.EmptyState
import com.example.tuf.presentation.components.getCategoryEmoji
import com.example.tuf.ui.theme.Spacing
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

/**
 * Categories management screen — view, add, and manage custom categories.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    viewModel: CategoryViewModel = koinViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var selectedTypeTab by remember { mutableStateOf(0) }
    val tabs = listOf("All", "Expense", "Income")

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { message -> snackbarHostState.showSnackbar(message) }
    }

    val filteredCategories = remember(uiState.categories, selectedTypeTab) {
        when (selectedTypeTab) {
            1 -> uiState.categories.filter { it.type == CategoryType.EXPENSE || it.type == CategoryType.BOTH }
            2 -> uiState.categories.filter { it.type == CategoryType.INCOME || it.type == CategoryType.BOTH }
            else -> uiState.categories
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Categories", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, "Back") } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            TabRow(selectedTabIndex = selectedTypeTab, containerColor = MaterialTheme.colorScheme.background) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTypeTab == index,
                        onClick = { selectedTypeTab = index },
                        text = { Text(title, fontWeight = if (selectedTypeTab == index) FontWeight.Bold else FontWeight.Normal) }
                    )
                }
            }

            if (filteredCategories.isEmpty()) {
                EmptyState(title = "No categories", emoji = "🗂️", subtitle = "Default categories will appear here")
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(Spacing.md),
                    verticalArrangement = Arrangement.spacedBy(Spacing.xs)
                ) {
                    items(filteredCategories, key = { it.id }) { category ->
                        CategoryItem(
                            category = category,
                            onDelete = if (category.isCustom) ({ viewModel.deleteCategory(category) }) else null
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryItem(
    category: Category,
    onDelete: (() -> Unit)? = null
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    if (showDeleteDialog && onDelete != null) {
        com.example.tuf.presentation.components.ConfirmDialog(
            title = "Delete Category",
            message = "Delete '${category.name}'? This may affect existing transactions.",
            confirmLabel = "Delete",
            isDestructive = true,
            onConfirm = { onDelete(); showDeleteDialog = false },
            onDismiss = { showDeleteDialog = false }
        )
    }

    ListItem(
        headlineContent = { Text(category.name, fontWeight = FontWeight.Medium) },
        supportingContent = {
            Text(
                when (category.type) {
                    CategoryType.INCOME -> "Income"
                    CategoryType.EXPENSE -> "Expense"
                    CategoryType.BOTH -> "Both"
                },
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        },
        leadingContent = {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(category.color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(getCategoryEmoji(category.iconName), style = MaterialTheme.typography.titleSmall)
            }
        },
        trailingContent = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (category.isCustom) {
                    Surface(
                        color = MaterialTheme.colorScheme.primary.copy(0.12f),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Text(
                            "Custom",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(Modifier.width(4.dp))
                    IconButton(onClick = { showDeleteDialog = true }, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    )
    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.12f))
}
