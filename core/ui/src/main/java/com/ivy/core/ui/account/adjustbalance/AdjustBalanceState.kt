package com.ivy.core.ui.account.adjustbalance

import androidx.compose.runtime.Immutable
import com.ivy.core.ui.account.adjustbalance.data.AdjustType

@Immutable
internal data class AdjustBalanceState(
    val adjustType: AdjustType,
)