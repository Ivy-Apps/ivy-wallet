package com.ivy.wallet.domain.action.transaction

import com.ivy.data.transaction.TransactionOld
import com.ivy.frp.action.FPAction
import com.ivy.frp.action.thenMap
import com.ivy.wallet.io.persistence.dao.TransactionDao
import javax.inject.Inject

class AllTrnsAct @Inject constructor(
    private val transactionDao: TransactionDao
) : FPAction<Unit, List<TransactionOld>>() {
    override suspend fun Unit.compose(): suspend () -> List<TransactionOld> = suspend {
        transactionDao.findAll()
    } thenMap { it.toDomain() }
}