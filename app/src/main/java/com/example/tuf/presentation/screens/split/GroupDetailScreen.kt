package com.example.tuf.presentation.screens.split

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupDetailScreen(
    groupId: Long,
    viewModel: SplitViewModel = koinViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToAddExpense: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(groupId) {
        viewModel.loadGroupDetails(groupId)
    }

    val group = uiState.currentGroup

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(group?.name ?: "Group Details", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, "Back") }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { onNavigateToAddExpense(groupId) }) {
                Icon(Icons.Default.Add, "Add Expense")
            }
        }
    ) { paddingValues ->
        if (group == null) return@Scaffold

        Column(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            // Balances Summary
            Card(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Balances", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Simple Balance Calculator
                    val balances = mutableMapOf<String, Double>()
                    group.members.forEach { balances[it] = 0.0 }
                    
                    uiState.currentExpenses.forEach { expense ->
                        val paidBy = expense.paidBy
                        val total = expense.totalAmount
                        balances[paidBy] = (balances[paidBy] ?: 0.0) + total
                        
                        expense.splits.forEach { (person, amount) ->
                            balances[person] = (balances[person] ?: 0.0) - amount
                        }
                    }

                    balances.forEach { (person, net) ->
                        val text = if (net > 0) "gets back ₹$net" else if (net < 0) "owes ₹${-net}" else "is settled up"
                        Text("$person $text")
                    }
                }
            }

            Text("Expenses", modifier = Modifier.padding(horizontal = 16.dp), style = MaterialTheme.typography.titleMedium)
            
            LazyColumn(contentPadding = PaddingValues(16.dp)) {
                items(uiState.currentExpenses) { expense ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(expense.description, fontWeight = FontWeight.Bold)
                            Text("Total: ₹${expense.totalAmount} paid by ${expense.paidBy}")
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp).fillMaxWidth(),
                            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Info, "Coming Soon")
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("More Features Coming Soon!", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Text("Settle up reminders, bill exports, and dynamic group links are on the way.", style = MaterialTheme.typography.bodySmall, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                        }
                    }
                    Spacer(modifier = Modifier.height(60.dp))
                }
            }
        }
    }
}
