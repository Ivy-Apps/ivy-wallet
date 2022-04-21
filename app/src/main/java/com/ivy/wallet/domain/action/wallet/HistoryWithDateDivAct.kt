package com.ivy.wallet.domain.action.wallet

import com.ivy.wallet.domain.action.Action
import com.ivy.wallet.domain.data.TransactionHistoryItem
import com.ivy.wallet.domain.fp.data.ClosedTimeRange
import com.ivy.wallet.domain.fp.data.WalletDAOs
import com.ivy.wallet.domain.fp.wallet.historyWithDateDividers
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