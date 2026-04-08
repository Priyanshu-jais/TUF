package com.example.tuf.presentation.screens.split

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tuf.domain.model.SplitExpense
import com.example.tuf.domain.model.SplitGroup
import com.example.tuf.domain.usecase.split.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class SplitUiState(
    val groups: List<SplitGroup> = emptyList(),
    val currentGroup: SplitGroup? = null,
    val currentExpenses: List<SplitExpense> = emptyList(),
    val isLoading: Boolean = false
)

class SplitViewModel(
    private val getGroupsUseCase: GetGroupsUseCase,
    private val addGroupUseCase: AddGroupUseCase,
    private val deleteGroupUseCase: DeleteGroupUseCase,
    private val getGroupExpensesUseCase: GetGroupExpensesUseCase,
    private val addSplitExpenseUseCase: AddSplitExpenseUseCase,
    private val deleteSplitExpenseUseCase: DeleteSplitExpenseUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SplitUiState())
    val uiState: StateFlow<SplitUiState> = _uiState.asStateFlow()

    init {
        loadGroups()
    }

    private fun loadGroups() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getGroupsUseCase().collectLatest { groups ->
                _uiState.update { it.copy(groups = groups, isLoading = false) }
            }
        }
    }

    fun loadGroupDetails(groupId: Long) {
        viewModelScope.launch {
            val group = _uiState.value.groups.find { it.id == groupId }
            _uiState.update { it.copy(currentGroup = group, isLoading = true) }
            
            getGroupExpensesUseCase(groupId).collectLatest { expenses ->
                _uiState.update { it.copy(currentExpenses = expenses, isLoading = false) }
            }
        }
    }

    fun addGroup(name: String, members: List<String>) {
        viewModelScope.launch {
            addGroupUseCase(SplitGroup(name = name, members = members, createdAt = System.currentTimeMillis()))
        }
    }

    fun addExpense(expense: SplitExpense) {
        viewModelScope.launch {
            addSplitExpenseUseCase(expense)
        }
    }

    fun deleteExpense(expenseId: Long) {
        viewModelScope.launch {
            deleteSplitExpenseUseCase(expenseId)
        }
    }

    fun deleteGroup(group: SplitGroup) {
        viewModelScope.launch {
            deleteGroupUseCase(group)
            _uiState.update { it.copy(currentGroup = null, currentExpenses = emptyList()) }
        }
    }
}
