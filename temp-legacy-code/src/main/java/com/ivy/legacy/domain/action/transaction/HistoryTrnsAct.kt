package com.ivy.wallet.domain.action.transaction

import com.ivy.core.datamodel.Transaction
import com.ivy.core.temp.toDomain
import com.ivy.frp.action.FPAction
import com.ivy.frp.action.thenMap
import com.ivy.persistence.db.dao.read.TransactionDao
import com.ivy.wallet.domain.pure.data.ClosedTimeRange
import javax.inject.Inject

class HistoryTrnsAct @Inject constructor(
    private val transactionDao: TransactionDao
) : FPAction<ClosedTimeRange, List<Transaction>>() {

    override suspend fun ClosedTimeRange.compose(): suspend () -> List<Transaction> = suspend {
        io {
            transactionDao.findAllBetween(
                startDate = from,
                endDate = to
            )
        }
    } thenMap { it.toDomain() }
}
