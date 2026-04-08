package com.example.tuf.presentation.screens.split

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupListScreen(
    viewModel: SplitViewModel = koinViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToGroup: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddGroupDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Split Groups", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, "Back") }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddGroupDialog = true }) {
                Icon(Icons.Default.Add, "Add Group")
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            if (uiState.groups.isEmpty() && !uiState.isLoading) {
                Text(
                    "No groups yet! Tap + to create one.",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                LazyColumn(contentPadding = PaddingValues(16.dp)) {
                    items(uiState.groups) { group ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                                .clickable { onNavigateToGroup(group.id) },
                        ) {
                            Row(modifier = Modifier.padding(16.dp)) {
                                Icon(Icons.Default.Group, null)
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(group.name, fontWeight = FontWeight.Bold)
                                    Text("${group.members.size} members", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showAddGroupDialog) {
            AddGroupDialog(
                onDismiss = { showAddGroupDialog = false },
                onSave = { name, members ->
                    viewModel.addGroup(name, members)
                    showAddGroupDialog = false
                }
            )
        }
    }
}

@Composable
fun AddGroupDialog(onDismiss: () -> Unit, onSave: (String, List<String>) -> Unit) {
    var groupName by remember { mutableStateOf("") }
    var memberNames by remember { mutableStateOf("") } // Comma separated

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New Split Group") },
        text = {
            Column {
                OutlinedTextField(
                    value = groupName,
                    onValueChange = { groupName = it },
                    label = { Text("Group Name") },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = memberNames,
                    onValueChange = { memberNames = it },
                    label = { Text("Members (comma separated)") },
                    placeholder = { Text("You, Friend A, Friend B") }
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val memberList = memberNames.split(",").map { it.trim() }.filter { it.isNotBlank() }
                if (groupName.isNotBlank() && memberList.isNotEmpty()) {
                    onSave(groupName, memberList)
                }
            }) { Text("Create") }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
