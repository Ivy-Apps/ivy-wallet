package com.ivy.wallet.domain.action.transaction

import com.ivy.wallet.domain.action.framework.Action
import com.ivy.wallet.domain.data.TransactionHistoryItem
import com.ivy.wallet.domain.pure.data.ClosedTimeRange
import com.ivy.wallet.domain.pure.data.WalletDAOs
import javax.inject.Inject

class HistoryWithDateDivAct @Inject constructor(
    private val walletDAOs: WalletDAOs
) : Action<HistoryWithDateDivAct.Input, List<TransactionHistoryItem>>() {

    override suspend fun Input.willDo(): List<TransactionHistoryItem> = io {
        historyWithDateDividers(
            walletDAOs = walletDAOs,
            baseCurrencyCode = baseCurrencyCode,
            range = timeRange
        )
    }

    data class Input(
        val timeRange: ClosedTimeRange,
        val baseCurrencyCode: String
    )
}