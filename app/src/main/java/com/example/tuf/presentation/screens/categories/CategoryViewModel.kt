package com.example.tuf.presentation.screens.categories

import com.example.tuf.core.base.BaseViewModel
import com.example.tuf.domain.model.Category
import com.example.tuf.domain.usecase.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update

data class CategoryUiState(
    val isLoading: Boolean = true,
    val categories: List<Category> = emptyList(),
    val error: String? = null
)

class CategoryViewModel(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val addCategoryUseCase: AddCategoryUseCase,
    private val updateCategoryUseCase: UpdateCategoryUseCase,
    private val deleteCategoryUseCase: DeleteCategoryUseCase
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(CategoryUiState())
    val uiState: StateFlow<CategoryUiState> = _uiState.asStateFlow()

    private val _events = Channel<String>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        safeLaunch {
            getCategoriesUseCase().collect { categories ->
                _uiState.update { it.copy(isLoading = false, categories = categories) }
            }
        }
    }

    fun addCategory(category: Category) {
        safeLaunch {
            addCategoryUseCase(category)
            _events.send("Category added")
        }
    }

    fun updateCategory(category: Category) {
        safeLaunch {
            updateCategoryUseCase(category)
            _events.send("Category updated")
        }
    }

    fun deleteCategory(category: Category) {
        safeLaunch {
            deleteCategoryUseCase(category)
            _events.send("Category deleted")
        }
    }

    override fun onError(throwable: Throwable) {
        _uiState.update { it.copy(isLoading = false, error = throwable.message) }
    }
}
