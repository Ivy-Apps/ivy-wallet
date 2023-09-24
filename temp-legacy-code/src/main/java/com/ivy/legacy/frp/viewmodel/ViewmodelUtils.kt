package com.ivy.frp.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Deprecated("Legacy code. Don't use it, please.")
fun <T> MutableStateFlow<T>.readOnly(): StateFlow<T> {
    return this
}