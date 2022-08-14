package com.ivy.wallet.domain.action.transaction

import com.ivy.data.transaction.TransactionOld
import com.ivy.frp.action.FPAction
import com.ivy.frp.action.thenMap
import com.ivy.wallet.io.persistence.dao.TransactionDao
import javax.inject.Inject

class DueTrnsAct @Inject constructor(
    private val transactionDao: TransactionDao
) : FPAction<com.ivy.base.ClosedTimeRange, List<TransactionOld>>() {

    override suspend fun com.ivy.base.ClosedTimeRange.compose(): suspend () -> List<TransactionOld> =
        suspend {
            io {
                transactionDao.findAllDueToBetween(
                    startDate = from,
                    endDate = to
                )
            }
        } thenMap { it.toDomain() }
}