package com.ivy.wallet.domain.action.account

import com.ivy.data.transaction.Transaction
import com.ivy.frp.action.FPAction
import com.ivy.frp.action.thenMap
import com.ivy.wallet.io.persistence.dao.TransactionDao
import java.util.*
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
        val range: com.ivy.base.ClosedTimeRange
    )
}

