package com.seiko.uistate

inline fun <R: Any, T : R> Result<T>.toUiState(
    transform: (data: T) -> UiState<R> = { UiState.success(it) },
): UiState<R> {
    return fold(
        onSuccess = { transform(it) },
        onFailure = { UiState.failure(it) },
    )
}
