package util

import com.seiko.uistate.UiState

data object EmptyList

fun <T> UiState.Companion.emptyList() = other<T>(EmptyList)

inline fun <T> UiState<T>.onEmptyList(action: () -> Unit) = apply {
    if (otherOrNull() == EmptyList) action()
}
