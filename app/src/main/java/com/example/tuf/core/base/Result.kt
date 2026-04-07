package com.example.tuf.core.base

/**
 * Sealed class representing the result of an operation.
 * Used throughout the domain and data layers to communicate outcomes.
 */
sealed class Result<out T> {
    /** Operation completed successfully with [data]. */
    data class Success<T>(val data: T) : Result<T>()

    /** Operation failed with an [exception] and optional [message]. */
    data class Error(
        val exception: Throwable,
        val message: String? = exception.localizedMessage
    ) : Result<Nothing>()

    /** Operation is in progress. */
    object Loading : Result<Nothing>()
}

/** Returns true if this result is [Result.Success]. */
val <T> Result<T>.isSuccess: Boolean get() = this is Result.Success

/** Returns true if this result is [Result.Error]. */
val <T> Result<T>.isError: Boolean get() = this is Result.Error

/** Returns true if this result is [Result.Loading]. */
val <T> Result<T>.isLoading: Boolean get() = this is Result.Loading

/** Returns the data if [Result.Success], otherwise null. */
fun <T> Result<T>.getOrNull(): T? = (this as? Result.Success)?.data

/** Returns the data if [Result.Success], otherwise [default]. */
fun <T> Result<T>.getOrDefault(default: T): T = (this as? Result.Success)?.data ?: default

/** Transforms the data inside [Result.Success] while preserving error/loading. */
fun <T, R> Result<T>.map(transform: (T) -> R): Result<R> = when (this) {
    is Result.Success -> Result.Success(transform(data))
    is Result.Error -> Result.Error(exception, message)
    is Result.Loading -> Result.Loading
}
