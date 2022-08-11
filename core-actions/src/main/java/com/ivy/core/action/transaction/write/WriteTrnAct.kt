package com.ivy.core.action.transaction.write

import com.ivy.data.transaction.Transaction
import com.ivy.frp.action.FPAction
import com.ivy.wallet.io.persistence.dao.TransactionDao
import javax.inject.Inject

class WriteTrnAct @Inject constructor(
    private val transactionDao: TransactionDao
) : FPAction<Transaction, Unit>() {
    override suspend fun Transaction.compose(): suspend () -> Unit = {
        transactionDao.save(mapToEntity(this))
    }

}