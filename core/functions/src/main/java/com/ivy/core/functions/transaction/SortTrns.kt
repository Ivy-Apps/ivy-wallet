package com.ivy.core.functions.transaction

import com.ivy.data.transaction.Transaction
import com.ivy.data.transaction.TrnTime
import java.time.LocalDateTime

fun chronological(trns: List<Transaction>): List<Transaction> =
    trns.sortedWith(
        compareBy<Transaction, LocalDateTime?>(nullsLast()) { (it.time as? TrnTime.Due)?.due }
            .thenByDescending { (it.time as? TrnTime.Actual)?.actual }
    )