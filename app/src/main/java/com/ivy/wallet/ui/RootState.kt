package com.ivy.wallet.ui

import androidx.compose.runtime.Immutable
import com.ivy.data.Theme

@Immutable
data class RootState(
    val appLocked: Boolean,
    val theme: Theme,
)