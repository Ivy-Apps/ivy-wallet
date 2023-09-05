package com.ivy.wallet.domain.action.transaction

import com.google.firebase.crashlytics.internal.model.ImmutableList
import com.ivy.frp.action.FPAction
import com.ivy.frp.then
import com.ivy.wallet.domain.data.TransactionHistoryItem
import com.ivy.wallet.domain.pure.data.ClosedTimeRange
import com.ivy.wallet.utils.toActualImmutableList
import javax.inject.Inject

class HistoryWithDateDivsAct @Inject constructor(
    private val historyTrnsAct: HistoryTrnsAct,
    private val trnsWithDateDivsAct: TrnsWithDateDivsAct
) : FPAction<HistoryWithDateDivsAct.Input, ImmutableList<TransactionHistoryItem>>() {

    override suspend fun Input.compose(): suspend () -> ImmutableList<TransactionHistoryItem> =
        suspend {
            range
        } then historyTrnsAct then { trns ->
            TrnsWithDateDivsAct.Input(
                baseCurrency = baseCurrency,
                transactions = trns
            )
        } then trnsWithDateDivsAct then { it.toActualImmutableList() }

    data class Input(
        val range: ClosedTimeRange,
        val baseCurrency: String
    )
}
