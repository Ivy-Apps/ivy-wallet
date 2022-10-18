package com.ivy.core.ui.account.adjustbalance

import com.ivy.core.ui.account.adjustbalance.data.AdjustType
import com.ivy.data.Value

internal sealed interface AdjustBalanceEvent {
    data class Initial(val accountId: String) : AdjustBalanceEvent

    data class AdjustTypeChange(val type: AdjustType) : AdjustBalanceEvent

    data class AdjustBalance(val balance: Value) : AdjustBalanceEvent
}