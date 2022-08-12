package com.ivy.core.action.transaction.read

import com.ivy.data.Period
import com.ivy.data.transaction.Transaction
import com.ivy.frp.action.FPAction
import com.ivy.frp.then
import com.ivy.wallet.io.persistence.dao.TransactionDao
import javax.inject.Inject

class DuelTrnsAct @Inject constructor(
    private val transactionDao: TransactionDao,
    private val transactionsAct: TrnsAct,
) : FPAction<Period, List<Transaction>>() {
    override suspend fun Period.compose(): suspend () -> List<Transaction> = suspend {
        TrnsAct.Input(
            period = this,
            query = transactionDao::findAllDueToBetween
        )
    } then transactionsAct

}