package com.example.tuf.presentation.screens.transactions

import androidx.lifecycle.viewModelScope
import com.example.tuf.core.base.BaseViewModel
import com.example.tuf.core.utils.Constants
import com.example.tuf.core.utils.DateUtils
import com.example.tuf.data.local.DataStoreManager
import com.example.tuf.domain.model.Transaction
import com.example.tuf.domain.model.TransactionType
import com.example.tuf.domain.usecase.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ViewModel for the Transaction List screen.
 * Handles filtering, search (with debounce), swipe-to-delete with undo, and navigation.
 */
class TransactionViewModel(
    private val getTransactionsByMonthUseCase: GetTransactionsByMonthUseCase,
    private val addTransactionUseCase: AddTransactionUseCase,
    private val updateTransactionUseCase: UpdateTransactionUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val searchTransactionsUseCase: SearchTransactionsUseCase,
    private val dataStoreManager: DataStoreManager
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(TransactionUiState(
        selectedMonth = DateUtils.currentMonth(),
        selectedYear = DateUtils.currentYear()
    ))
    val uiState: StateFlow<TransactionUiState> = _uiState.asStateFlow()

    private val _events = Channel<TransactionEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private var searchJob: Job? = null
    private var pendingDeleteJob: Job? = null
    private var pendingDeleteTransaction: Transaction? = null

    init {
        observeCategories()
        observeTransactions()
        observeCurrency()
    }

    private fun observeCurrency() {
        safeLaunch {
            dataStoreManager.currencySymbol.collect { symbol ->
                _uiState.update { it.copy(currencySymbol = symbol) }
            }
        }
    }

    private fun observeCategories() {
        safeLaunch {
            getCategoriesUseCase().collect { categories ->
                _uiState.update { it.copy(categories = categories) }
            }
        }
    }

    private fun observeTransactions() {
        safeLaunch {
            _uiState.map { Pair(it.selectedMonth, it.selectedYear) }
                .distinctUntilChanged()
                .flatMapLatest { (month, year) ->
                    getTransactionsByMonthUseCase(month, year)
                }
                .collect { transactions ->
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            transactions = transactions,
                            filteredTransactions = applyFilters(transactions, state)
                        )
                    }
                }
        }
    }

    fun onEvent(event: TransactionUiEvent) {
        when (event) {
            is TransactionUiEvent.FilterByType -> {
                _uiState.update { state ->
                    val filtered = applyFilters(state.transactions, state.copy(selectedType = event.type))
                    state.copy(selectedType = event.type, filteredTransactions = filtered)
                }
            }
            is TransactionUiEvent.FilterByCategory -> {
                _uiState.update { state ->
                    val filtered = applyFilters(state.transactions, state.copy(selectedCategoryId = event.categoryId))
                    state.copy(selectedCategoryId = event.categoryId, filteredTransactions = filtered)
                }
            }
            is TransactionUiEvent.MonthChanged -> {
                _uiState.update {
                    it.copy(
                        selectedMonth = event.month,
                        selectedYear = event.year,
                        isLoading = true
                    )
                }
            }
            is TransactionUiEvent.SearchQueryChanged -> {
                _uiState.update { it.copy(searchQuery = event.query) }
                searchJob?.cancel()
                if (event.query.isBlank()) {
                    _uiState.update { state ->
                        state.copy(filteredTransactions = applyFilters(state.transactions, state))
                    }
                } else {
                    searchJob = safeLaunch {
                        delay(Constants.SEARCH_DEBOUNCE_MS)
                        searchTransactionsUseCase(event.query).first().let { results ->
                            _uiState.update { it.copy(filteredTransactions = results) }
                        }
                    }
                }
            }
            is TransactionUiEvent.ToggleSearch -> {
                _uiState.update { it.copy(isSearchActive = event.active, searchQuery = if (!event.active) "" else it.searchQuery) }
                if (!event.active) {
                    _uiState.update { state ->
                        state.copy(filteredTransactions = applyFilters(state.transactions, state))
                    }
                }
            }
            is TransactionUiEvent.DeleteTransaction -> {
                softDelete(event.transaction)
            }
            is TransactionUiEvent.UndoDelete -> {
                pendingDeleteJob?.cancel()
                pendingDeleteTransaction = null
                _uiState.update { state ->
                    val restored = pendingDeleteTransaction ?: return@update state
                    state.copy(
                        transactions = (state.transactions + restored).sortedByDescending { it.date },
                        filteredTransactions = (state.filteredTransactions + restored).sortedByDescending { it.date }
                    )
                }
            }
            is TransactionUiEvent.EditTransaction -> {
                safeLaunch { _events.send(TransactionEvent.NavigateToEdit(event.transactionId)) }
            }
        }
    }

    private fun softDelete(transaction: Transaction) {
        pendingDeleteTransaction = transaction
        _uiState.update { state ->
            state.copy(
                transactions = state.transactions.filter { it.id != transaction.id },
                filteredTransactions = state.filteredTransactions.filter { it.id != transaction.id }
            )
        }
        pendingDeleteJob?.cancel()
        pendingDeleteJob = viewModelScope.launch {
            safeLaunch {
                _events.send(
                    TransactionEvent.ShowSnackbar(
                        message = "Transaction deleted",
                        actionLabel = "UNDO",
                        onAction = { onEvent(TransactionUiEvent.UndoDelete) }
                    )
                )
            }
            delay(Constants.UNDO_DELETE_DURATION_MS)
            // Commit the delete
            deleteTransactionUseCase(transaction)
            pendingDeleteTransaction = null
        }
    }

    private fun applyFilters(transactions: List<Transaction>, state: TransactionUiState): List<Transaction> {
        return transactions.filter { tx ->
            (state.selectedType == null || tx.type == state.selectedType) &&
                    (state.selectedCategoryId == null || tx.category.id == state.selectedCategoryId)
        }
    }

    override fun onError(throwable: Throwable) {
        _uiState.update { it.copy(isLoading = false, error = throwable.message) }
    }
}
