package com.ivy.wallet.domain.action.loan

import com.ivy.core.data.db.read.LoanDao
import com.ivy.core.data.model.Loan
import com.ivy.frp.action.FPAction
import java.util.UUID
import javax.inject.Inject

class LoanByIdAct @Inject constructor(
    private val loanDao: LoanDao
) : FPAction<UUID, Loan?>() {
    override suspend fun UUID.compose(): suspend () -> Loan? = suspend {
        loanDao.findById(this)?.toDomain()
    }
}
