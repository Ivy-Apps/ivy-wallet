package com.ivy.wallet.domain.action.loan

import com.ivy.frp.action.FPAction
import com.ivy.core.data.model.Loan
import com.ivy.core.data.db.read.LoanDao
import java.util.*
import javax.inject.Inject

class LoanByIdAct @Inject constructor(
    private val loanDao: LoanDao
) : FPAction<UUID, Loan?>() {
    override suspend fun UUID.compose(): suspend () -> Loan? = suspend {
        loanDao.findById(this)?.toDomain()
    }
}
