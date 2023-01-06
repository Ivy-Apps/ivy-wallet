package com.ivy.core.ui.amount.data

import androidx.compose.runtime.Immutable

@Immutable
data class CalculatorResultUi(
    val result: String,
    val isError: Boolean
)