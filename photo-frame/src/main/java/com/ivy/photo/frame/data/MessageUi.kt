package com.ivy.photo.frame.data

import androidx.compose.runtime.Immutable

@Immutable
sealed interface MessageUi {
    object None : MessageUi
    data class Loading(val message: String) : MessageUi
    data class Error(val message: String) : MessageUi
    data class Success(val message: String) : MessageUi
}