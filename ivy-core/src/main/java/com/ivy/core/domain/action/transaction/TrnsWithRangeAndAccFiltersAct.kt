package com.ivy.wallet.domain.action.transaction

import com.ivy.core.data.model.FromToTimeRange
import com.ivy.frp.action.FPAction
import com.ivy.frp.action.thenFilter
import com.ivy.wallet.domain.data.core.Transaction
import com.ivy.wallet.io.persistence.dao.TransactionDao
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
        val range: FromToTimeRange,
        val accountIdFilterSet: Set<UUID>
    )
}
