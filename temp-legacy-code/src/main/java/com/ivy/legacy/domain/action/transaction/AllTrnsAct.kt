package com.ivy.wallet.domain.action.transaction

import com.ivy.domain.datamodel.Transaction
import com.ivy.domain.temp.toDomain
import com.ivy.frp.action.FPAction
import com.ivy.frp.action.thenMap
import com.ivy.persistence.db.dao.read.TransactionDao
import javax.inject.Inject

class AllTrnsAct @Inject constructor(
    private val transactionDao: TransactionDao
) : FPAction<Unit, List<Transaction>>() {
    override suspend fun Unit.compose(): suspend () -> List<Transaction> = suspend {
        transactionDao.findAll()
    } thenMap { it.toDomain() }
}
