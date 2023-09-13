package com.ivy.frp.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

fun <T> MutableStateFlow<T>.readOnly(): StateFlow<T> {
    return this
}