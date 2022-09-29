package com.ivy.wallet.ui

import androidx.compose.runtime.Immutable

@Immutable
data class RootState(
    val appLocked: Boolean
)