package com.seiko.uistate

import androidx.compose.runtime.Immutable

sealed interface UiLoadingState {
    object NotLoading : UiLoadingState
    object Loading : UiLoadingState

    @Immutable
    data class Error(val error: Throwable) : UiLoadingState
}

@Immutable
data class CombinedUiLoadingState(
    val refresh: UiLoadingState,
    val remote: UiLoadingState,
)

internal inline fun UiLoadingState.toCombined() = CombinedUiLoadingState(
    refresh = this,
    remote = UiLoadingState.NotLoading,
)