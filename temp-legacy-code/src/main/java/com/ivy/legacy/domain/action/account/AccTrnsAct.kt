package com.ivy.wallet.domain.action.account

import com.ivy.core.data.db.read.TransactionDao
import com.ivy.core.datamodel.Transaction
import com.ivy.frp.action.FPAction
import com.ivy.frp.action.thenMap
import com.ivy.wallet.domain.pure.data.ClosedTimeRange
import java.util.UUID
import javax.inject.Inject

class AccTrnsAct @Inject constructor(
    private val transactionDao: TransactionDao
) : FPAction<AccTrnsAct.Input, List<Transaction>>() {
    override suspend fun Input.compose(): suspend () -> List<Transaction> = suspend {
        io {
            transactionDao.findAllByAccountAndBetween(
                accountId = accountId,
                startDate = range.from,
                endDate = range.to
            ) + transactionDao.findAllToAccountAndBetween(
                toAccountId = accountId,
                startDate = range.from,
                endDate = range.to
            )
        }
    } thenMap {
        it.toDomain()
    }

    class Input(
        val accountId: UUID,
        val range: ClosedTimeRange
    )
}
