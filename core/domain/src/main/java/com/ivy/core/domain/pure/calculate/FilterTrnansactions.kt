package com.ivy.core.domain.pure.calculate

import com.ivy.data.transaction.Transaction
import com.ivy.data.transaction.TrnPurpose
import com.ivy.data.transaction.TrnState

fun List<Transaction>.filter(
    includeTransfers: Boolean,
    includeHidden: Boolean,
): List<Transaction> = this.filter {
    it.transferFilter(includeTransfers = includeTransfers) &&
            it.hiddenFilter(includeHidden = includeHidden)
}

private fun Transaction.transferFilter(includeTransfers: Boolean): Boolean =
    includeTransfers || when (purpose) {
        TrnPurpose.TransferFrom, TrnPurpose.TransferTo -> false
        else -> true
    }

private fun Transaction.hiddenFilter(includeHidden: Boolean): Boolean =
    includeHidden || state != TrnState.Hidden