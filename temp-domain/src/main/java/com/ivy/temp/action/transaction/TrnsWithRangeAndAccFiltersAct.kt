package com.ivy.wallet.domain.action.transaction

import com.ivy.base.FromToTimeRange
import com.ivy.data.transaction.TransactionOld
import com.ivy.frp.action.FPAction
import com.ivy.frp.action.thenFilter
import com.ivy.wallet.io.persistence.dao.TransactionDao
import java.util.*
import javax.inject.Inject

class TrnsWithRangeAndAccFiltersAct @Inject constructor(
    private val transactionDao: TransactionDao
) : FPAction<TrnsWithRangeAndAccFiltersAct.Input, List<TransactionOld>>() {

    override suspend fun Input.compose(): suspend () -> List<TransactionOld> = suspend {
        transactionDao.findAllBetween(range.from(), range.to())
            .map { it.toDomain() }
    } thenFilter {
        accountIdFilterSet.contains(it.accountId) || accountIdFilterSet.contains(it.toAccountId)
    }

    data class Input(
        val range: FromToTimeRange,
        val accountIdFilterSet: Set<UUID>
    )
}