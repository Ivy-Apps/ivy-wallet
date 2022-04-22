package com.ivy.wallet.domain.action.transaction

import com.ivy.fp.action.FPAction
import com.ivy.fp.action.thenMap
import com.ivy.wallet.domain.data.core.Transaction
import com.ivy.wallet.domain.pure.data.ClosedTimeRange
import com.ivy.wallet.io.persistence.dao.TransactionDao
import javax.inject.Inject

class HistoryTrnsAct @Inject constructor(
    private val transactionDao: TransactionDao
) : FPAction<HistoryTrnsAct.Input, List<Transaction>>() {

    override suspend fun Input.compose(): suspend () -> List<Transaction> = suspend {
        io {
            transactionDao.findAllBetween(
                startDate = range.from,
                endDate = range.to
            )
        }
    } thenMap { it.toDomain() }

    data class Input(
        val range: ClosedTimeRange
    )
}