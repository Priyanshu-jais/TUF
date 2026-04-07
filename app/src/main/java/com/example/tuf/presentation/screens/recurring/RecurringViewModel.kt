package com.example.tuf.presentation.screens.recurring

import com.example.tuf.core.base.BaseViewModel
import com.example.tuf.domain.model.Category
import com.example.tuf.domain.model.RecurringTransaction
import com.example.tuf.domain.usecase.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

data class RecurringUiState(
    val isLoading: Boolean = true,
    val recurringTransactions: List<RecurringTransaction> = emptyList(),
    val dueToday: List<RecurringTransaction> = emptyList(),
    val categories: List<Category> = emptyList(),
    val error: String? = null
)

class RecurringViewModel(
    private val getRecurringTransactionsUseCase: GetRecurringTransactionsUseCase,
    private val addRecurringTransactionUseCase: AddRecurringTransactionUseCase,
    private val updateRecurringUseCase: UpdateRecurringUseCase,
    private val deleteRecurringUseCase: DeleteRecurringUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(RecurringUiState())
    val uiState: StateFlow<RecurringUiState> = _uiState.asStateFlow()

    private val _events = Channel<String>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        observeRecurring()
        observeCategories()
    }

    private fun observeRecurring() {
        safeLaunch {
            getRecurringTransactionsUseCase().collect { list ->
                val now = System.currentTimeMillis()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        recurringTransactions = list,
                        dueToday = list.filter { r -> r.isActive && r.nextDueDate <= now }
                    )
                }
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

    fun toggleActive(recurring: RecurringTransaction) {
        safeLaunch {
            updateRecurringUseCase(recurring.copy(isActive = !recurring.isActive))
        }
    }

    fun delete(recurring: RecurringTransaction) {
        safeLaunch {
            deleteRecurringUseCase(recurring)
            _events.send("Recurring transaction deleted")
        }
    }

    override fun onError(throwable: Throwable) {
        _uiState.update { it.copy(isLoading = false, error = throwable.message) }
    }
}
