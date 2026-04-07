package com.example.tuf.core.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Base ViewModel that provides common error handling and coroutine launch utilities.
 * All ViewModels in the Finance Manager app extend this class.
 */
abstract class BaseViewModel : ViewModel() {

    /**
     * A [CoroutineExceptionHandler] that captures uncaught exceptions from [launch].
     * Override [onError] to handle errors in subclasses.
     */
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError(throwable)
    }

    /**
     * Called when an unhandled coroutine exception occurs.
     * Subclasses should override to update their error state.
     */
    protected open fun onError(throwable: Throwable) {}

    /**
     * Launches a coroutine in [viewModelScope] with the shared [exceptionHandler].
     * Returns the [Job] so callers can cancel it if needed (e.g., search debounce).
     */
    protected fun safeLaunch(block: suspend CoroutineScope.() -> Unit): Job {
        return viewModelScope.launch(exceptionHandler, block = block)
    }
}
