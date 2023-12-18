package com.seiko.uistate

import androidx.compose.runtime.State

inline fun <R, T : R> State<UiState<T>>.getOrElse(action: (UiState<T>) -> T): T {
    return value.getOrElse(action)
}
