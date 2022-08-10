package com.ivy.wallet.domain.action.transaction

import com.ivy.data.transaction.TransactionOld
import com.ivy.frp.action.FPAction
import com.ivy.frp.then
import com.ivy.wallet.io.persistence.dao.TransactionDao
import java.util.*
import javax.inject.Inject

class TrnByIdAct @Inject constructor(
    private val transactionDao: TransactionDao
) : FPAction<UUID, TransactionOld?>() {
    override suspend fun UUID.compose(): suspend () -> TransactionOld? = suspend {
        this //transactionId
    } then transactionDao::findById then {
        it?.toDomain()
    }
}