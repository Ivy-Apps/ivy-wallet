package com.ivy.core.ui.algorithm.trnhistory

import com.ivy.core.domain.algorithm.calc.data.RawStats
import com.ivy.core.domain.algorithm.calc.rawStats
import com.ivy.core.persistence.algorithm.calc.CalcTrn
import com.ivy.core.persistence.algorithm.trnhistory.CalcHistoryTrnView
import com.ivy.core.persistence.entity.trn.data.TrnTimeType
import com.ivy.data.transaction.TrnPurpose

fun calcHistoryTrnsToRawStats(
    calcHistoryTrns: List<CalcHistoryTrnView>,
): RawStats {
    fun mustBeExcluded(purpose: TrnPurpose?): Boolean = when (purpose) {
        TrnPurpose.TransferFrom, TrnPurpose.TransferTo -> true
        TrnPurpose.Fee, TrnPurpose.AdjustBalance, null -> false
    }

    val calcTrns = calcHistoryTrns.mapNotNull {
        // Hidden (state = TrnStatHidde) transactions are excluded on DB level
        if (it.timeType == TrnTimeType.Due || mustBeExcluded(it.purpose)) {
            null
        } else it.toCalcTrn()
    }
    return rawStats(calcTrns)
}

// TODO: Wasting some memory, investigate!
fun CalcHistoryTrnView.toCalcTrn() = CalcTrn(
    amount = amount,
    currency = currency,
    type = type,
    time = time
)