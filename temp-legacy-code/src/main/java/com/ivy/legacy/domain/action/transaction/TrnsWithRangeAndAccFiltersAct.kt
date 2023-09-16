package com.ivy.wallet.domain.action.transaction

import com.ivy.frp.action.FPAction
import com.ivy.frp.action.thenFilter
import com.ivy.core.data.model.Transaction
import com.ivy.core.data.db.read.TransactionDao
import java.util.UUID
import javax.inject.Inject

class TrnsWithRangeAndAccFiltersAct @Inject constructor(
    private val transactionDao: TransactionDao
) : FPAction<TrnsWithRangeAndAccFiltersAct.Input, List<Transaction>>() {

    override suspend fun Input.compose(): suspend () -> List<Transaction> = suspend {
        transactionDao.findAllBetween(range.from(), range.to())
            .map { it.toDomain() }
    } thenFilter {
        accountIdFilterSet.contains(it.accountId) || accountIdFilterSet.contains(it.toAccountId)
    }

    data class Input(
        val range: com.ivy.legacy.data.model.FromToTimeRange,
        val accountIdFilterSet: Set<UUID>
    )
}
