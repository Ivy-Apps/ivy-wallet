package com.ivy.core.functions.transaction

import com.ivy.data.transaction.Transaction
import com.ivy.data.transaction.TrnTime

fun sortTrns(trns: List<Transaction>): List<Transaction> =
    trns.sortedWith(
        compareByDescending<Transaction> { (it.time as? TrnTime.Due)?.due }
            .thenByDescending { (it.time as? TrnTime.Actual)?.actual }
    )