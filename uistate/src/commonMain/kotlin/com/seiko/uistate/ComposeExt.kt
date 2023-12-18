package com.seiko.uistate

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember

@Composable
inline fun <T> State<T?>.asUiState(
): State<UiState<T>> {
    return asUiState(mapper = { UiState.success(it) })
}

@Composable
inline fun <T, R> State<T?>.asUiState(
    crossinline mapper: (T) -> UiState<R>,
): State<UiState<R>> {
    return remember {
        derivedStateOf {
            value?.let(mapper) ?: UiState.loading()
        }
    }
}

inline fun <R, T : R> State<UiState<T>>.getOrElse(action: (UiState<T>) -> T): T {
    return value.getOrElse(action)
}