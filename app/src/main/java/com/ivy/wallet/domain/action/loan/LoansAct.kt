package com.ivy.wallet.domain.action.loan

import com.ivy.frp.action.FPAction
import com.ivy.frp.action.thenMap
import com.ivy.wallet.domain.data.core.Loan
import com.ivy.wallet.io.persistence.dao.LoanDao
import javax.inject.Inject

class LoansAct @Inject constructor(
    private val loanDao: LoanDao
) : FPAction<Unit, List<Loan>>() {
    override suspend fun Unit.compose(): suspend () -> List<Loan> = suspend {
        loanDao.findAll()
    } thenMap { it.toDomain() }
}