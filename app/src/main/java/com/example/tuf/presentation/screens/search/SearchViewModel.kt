package com.example.tuf.presentation.screens.search

import com.example.tuf.core.base.BaseViewModel
import com.example.tuf.core.utils.Constants
import com.example.tuf.domain.model.Category
import com.example.tuf.domain.model.Transaction
import com.example.tuf.domain.model.TransactionType
import com.example.tuf.domain.usecase.GetCategoriesUseCase
import com.example.tuf.domain.usecase.SearchTransactionsUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update

data class SearchUiState(
    val query: String = "",
    val results: List<Transaction> = emptyList(),
    val categories: List<Category> = emptyList(),
    val filterType: TransactionType? = null,
    val filterCategoryId: Long? = null,
    val isLoading: Boolean = false,
    val hasSearched: Boolean = false
)

class SearchViewModel(
    private val searchTransactionsUseCase: SearchTransactionsUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        safeLaunch {
            getCategoriesUseCase().collect { categories ->
                _uiState.update { it.copy(categories = categories) }
            }
        }
    }

    fun onQueryChanged(query: String) {
        _uiState.update { it.copy(query = query, isLoading = query.isNotBlank()) }
        searchJob?.cancel()
        if (query.isBlank()) {
            _uiState.update { it.copy(results = emptyList(), isLoading = false, hasSearched = false) }
            return
        }
        searchJob = safeLaunch {
            delay(Constants.SEARCH_DEBOUNCE_MS)
            val results = searchTransactionsUseCase(query).first()
            val filtered = applyFilters(results)
            _uiState.update { it.copy(results = filtered, isLoading = false, hasSearched = true) }
        }
    }

    fun setTypeFilter(type: TransactionType?) {
        _uiState.update { it.copy(filterType = type) }
        refreshResults()
    }

    fun setCategoryFilter(categoryId: Long?) {
        _uiState.update { it.copy(filterCategoryId = categoryId) }
        refreshResults()
    }

    private fun refreshResults() {
        safeLaunch {
            if (_uiState.value.query.isBlank()) return@safeLaunch
            val results = searchTransactionsUseCase(_uiState.value.query).first()
            _uiState.update { it.copy(results = applyFilters(results)) }
        }
    }

    private fun applyFilters(transactions: List<Transaction>): List<Transaction> {
        val state = _uiState.value
        return transactions.filter { tx ->
            (state.filterType == null || tx.type == state.filterType) &&
                    (state.filterCategoryId == null || tx.category.id == state.filterCategoryId)
        }
    }

    override fun onError(throwable: Throwable) {
        _uiState.update { it.copy(isLoading = false) }
    }
}
