package com.seiko.uistate

import kotlin.jvm.JvmField
import kotlin.jvm.JvmInline

@JvmInline
value class UiState<out T> internal constructor(
    private val data: Any?,
) {
    val isLoading: Boolean
        get() = data is Loading

    val isFailure: Boolean
        get() = data is Failure

    val isSuccess: Boolean
        get() = data is Success<*>

    val isOther: Boolean
        get() = data is Other<*>

    @Suppress("UNCHECKED_CAST")
    fun getOrNull(): T? =
        when (data) {
            is Success<*> -> data.data as T
            else -> null
        }

    fun exceptionOrNull(): Throwable? =
        when (data) {
            is Failure -> data.exception
            else -> null
        }

    fun otherOrNull(): Any? =
        when (data) {
            is Other<*> -> data.data
            else -> null
        }

    fun <R> swap(): UiState<R> {
        if (isSuccess) throw RuntimeException("UiState.Success can't swap")
        return UiState(data)
    }

    companion object {

        fun <T> loading(): UiState<T> {
            return UiState(Loading)
        }

        fun <T> success(data: T): UiState<T> {
            return UiState(Success(data))
        }

        fun <T> failure(error: Throwable): UiState<T> {
            return UiState(Failure(error))
        }

        fun <T> other(data: Any?): UiState<T> {
            return UiState(data)
        }
    }

    data object Loading

    class Failure(
        @JvmField
        val exception: Throwable,
    ) {
        override fun equals(other: Any?): Boolean = other is Failure && exception == other.exception
        override fun hashCode(): Int = exception.hashCode()
        override fun toString(): String = "Failure($exception)"
    }

    class Success<out T>(
        @JvmField
        val data: T,
    ) {
        override fun equals(other: Any?): Boolean = other is Success<*> && data == other.data
        override fun hashCode(): Int = data.hashCode()
        override fun toString(): String = "Success($data)"
    }

    class Other<out T>(
        @JvmField
        val data: T,
    ) {
        override fun equals(other: Any?): Boolean = other is Other<*> && data == other.data
        override fun hashCode(): Int = data.hashCode()
        override fun toString(): String = "Other($data)"
    }
}

inline fun <T> UiState<T>.onLoading(action: () -> Unit): UiState<T> = apply {
    if (isLoading) action()
}

inline fun <T> UiState<T>.onFailure(action: (exception: Throwable) -> Unit): UiState<T> = apply {
    exceptionOrNull()?.let(action)
}

inline fun <T> UiState<T>.onSuccess(action: (data: T) -> Unit): UiState<T> = apply {
    getOrNull()?.let(action)
}

inline fun <T> UiState<T>.onOther(action: (data: Any?) -> Unit): UiState<T> = apply {
    otherOrNull()?.let(action)
}

inline fun <R, T : R> UiState<T>.getOrElse(action: (UiState<T>) -> R): R {
    return getOrNull() ?: action(this)
}
