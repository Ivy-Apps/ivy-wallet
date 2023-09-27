package com.ivy.wallet.domain.action.loan

import com.ivy.core.datamodel.Loan
import com.ivy.core.temp.toDomain
import com.ivy.frp.action.FPAction
import com.ivy.persistence.db.dao.read.LoanDao
import java.util.UUID
import javax.inject.Inject

class LoanByIdAct @Inject constructor(
    private val loanDao: LoanDao
) : FPAction<UUID, Loan?>() {
    override suspend fun UUID.compose(): suspend () -> Loan? = suspend {
        loanDao.findById(this)?.toDomain()
    }
}
