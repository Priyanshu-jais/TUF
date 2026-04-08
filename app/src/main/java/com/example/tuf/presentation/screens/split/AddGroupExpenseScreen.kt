package com.example.tuf.presentation.screens.split

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.tuf.domain.model.SplitExpense
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGroupExpenseScreen(
    groupId: Long,
    viewModel: SplitViewModel = koinViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(groupId) {
        viewModel.loadGroupDetails(groupId)
    }

    val group = uiState.currentGroup
    var description by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var splitType by remember { mutableStateOf("EQUAL") }
    var paidBy by remember { mutableStateOf("") }

    val members = group?.members ?: emptyList()
    var unequalSplits = remember { mutableStateMapOf<String, String>() }

    // Init paidBy to first member
    LaunchedEffect(members) {
        if (members.isNotEmpty() && paidBy.isEmpty()) {
            paidBy = members[0]
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Split Expense", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, "Back") }
                }
            )
        }
    ) { paddingValues ->
        if (group == null) return@Scaffold

        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("What was it for?") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it },
                label = { Text("Total Amount") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Text("Paid By", style = MaterialTheme.typography.titleSmall)
            var expanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                OutlinedTextField(
                    value = paidBy,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    members.forEach { member ->
                        DropdownMenuItem(
                            text = { Text(member) },
                            onClick = { paidBy = member; expanded = false }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Split Type", style = MaterialTheme.typography.titleSmall)
            Row {
                RadioButton(selected = splitType == "EQUAL", onClick = { splitType = "EQUAL" })
                Text("Equally", modifier = Modifier.padding(top = 12.dp))
                Spacer(modifier = Modifier.width(16.dp))
                RadioButton(selected = splitType == "UNEQUAL", onClick = { splitType = "UNEQUAL" })
                Text("Unequally", modifier = Modifier.padding(top = 12.dp))
            }

            if (splitType == "UNEQUAL") {
                Spacer(modifier = Modifier.height(8.dp))
                members.forEach { member ->
                    OutlinedTextField(
                        value = unequalSplits[member] ?: "",
                        onValueChange = { unequalSplits[member] = it },
                        label = { Text("$member's share") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull() ?: 0.0
                    if (amount > 0 && description.isNotBlank()) {
                        val splits = mutableMapOf<String, Double>()
                        if (splitType == "EQUAL") {
                            val perPerson = amount / members.size
                            members.forEach { splits[it] = perPerson }
                        } else {
                            members.forEach { splits[it] = unequalSplits[it]?.toDoubleOrNull() ?: 0.0 }
                        }

                        val expense = SplitExpense(
                            groupId = groupId,
                            description = description,
                            totalAmount = amount,
                            paidBy = paidBy,
                            splitType = splitType,
                            splits = splits,
                            date = System.currentTimeMillis()
                        )
                        viewModel.addExpense(expense)
                        onNavigateBack()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Save Split Expense")
            }
        }
    }
}
