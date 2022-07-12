package com.ivy.wallet.domain.action.viewmodel.home

import com.ivy.frp.action.FPAction
import com.ivy.wallet.io.persistence.dao.TransactionDao
import javax.inject.Inject

class HasTrnsAct @Inject constructor(
    private val transactionDao: TransactionDao
) : FPAction<Unit, Boolean>() {
    override suspend fun Unit.compose(): suspend () -> Boolean = suspend {
        io {
            transactionDao.findAll_LIMIT_1().isNotEmpty()
        }
    }
}