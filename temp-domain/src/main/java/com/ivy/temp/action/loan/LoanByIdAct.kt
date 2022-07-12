package com.ivy.wallet.domain.action.loan

import com.ivy.data.loan.Loan
import com.ivy.frp.action.FPAction
import com.ivy.wallet.io.persistence.dao.LoanDao
import java.util.*
import javax.inject.Inject

class LoanByIdAct @Inject constructor(
    private val loanDao: LoanDao
) : FPAction<UUID, Loan?>() {
    override suspend fun UUID.compose(): suspend () -> Loan? = suspend {
        loanDao.findById(this)?.toDomain()
    }
}