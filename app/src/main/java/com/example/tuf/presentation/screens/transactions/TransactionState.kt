package com.example.tuf.presentation.screens.transactions

import com.example.tuf.domain.model.Category
import com.example.tuf.domain.model.Transaction
import com.example.tuf.domain.model.TransactionType

data class TransactionUiState(
    val isLoading: Boolean = true,
    val transactions: List<Transaction> = emptyList(),
    val filteredTransactions: List<Transaction> = emptyList(),
    val categories: List<Category> = emptyList(),
    val selectedType: TransactionType? = null,
    val selectedCategoryId: Long? = null,
    val searchQuery: String = "",
    val isSearchActive: Boolean = false,
    val selectedMonth: Int = 0,
    val selectedYear: Int = 0,
    val pendingDeleteTransaction: Transaction? = null,
    val currencySymbol: String = "₹",
    val error: String? = null
)

sealed class TransactionEvent {
    data class ShowSnackbar(val message: String, val actionLabel: String? = null, val onAction: (() -> Unit)? = null) : TransactionEvent()
    object NavigateToAddTransaction : TransactionEvent()
    data class NavigateToEdit(val transactionId: Long) : TransactionEvent()
}

sealed class TransactionUiEvent {
    data class FilterByType(val type: TransactionType?) : TransactionUiEvent()
    data class FilterByCategory(val categoryId: Long?) : TransactionUiEvent()
    data class MonthChanged(val month: Int, val year: Int) : TransactionUiEvent()
    data class SearchQueryChanged(val query: String) : TransactionUiEvent()
    data class ToggleSearch(val active: Boolean) : TransactionUiEvent()
    data class DeleteTransaction(val transaction: Transaction) : TransactionUiEvent()
    object UndoDelete : TransactionUiEvent()
    data class EditTransaction(val transactionId: Long) : TransactionUiEvent()
}
