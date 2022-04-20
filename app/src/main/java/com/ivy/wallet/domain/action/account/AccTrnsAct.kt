package com.ivy.wallet.domain.action.account

import com.ivy.wallet.domain.action.Action
import com.ivy.wallet.domain.data.entity.Transaction
import com.ivy.wallet.domain.fp.data.ClosedTimeRange
import com.ivy.wallet.io.persistence.dao.TransactionDao
import java.util.*
import javax.inject.Inject

class AccTrnsAct @Inject constructor(
    private val transactionDao: TransactionDao
) : Action<AccTrnsAct.Input, List<Transaction>>() {
    override suspend fun Input.willDo(): List<Transaction> = io {
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

    data class Input(
        val accountId: UUID,
        val range: ClosedTimeRange
    )
}