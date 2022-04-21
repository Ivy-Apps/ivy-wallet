package com.ivy.wallet.domain.action.account

import com.ivy.wallet.domain.action.FPAction
import com.ivy.wallet.domain.data.entity.Transaction
import com.ivy.wallet.domain.fp.data.ClosedTimeRange
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
    }

    class Input(
        val accountId: UUID,
        val range: ClosedTimeRange
    )
}