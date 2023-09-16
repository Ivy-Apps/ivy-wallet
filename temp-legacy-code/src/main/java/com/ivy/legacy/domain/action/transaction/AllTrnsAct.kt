package com.ivy.wallet.domain.action.transaction

import com.ivy.core.data.db.read.TransactionDao
import com.ivy.core.data.model.Transaction
import com.ivy.frp.action.FPAction
import com.ivy.frp.action.thenMap
import javax.inject.Inject

class AllTrnsAct @Inject constructor(
    private val transactionDao: TransactionDao
) : FPAction<Unit, List<Transaction>>() {
    override suspend fun Unit.compose(): suspend () -> List<Transaction> = suspend {
        transactionDao.findAll()
    } thenMap { it.toDomain() }
}
