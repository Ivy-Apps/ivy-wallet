package com.ivy.wallet.domain.action.loan

import com.ivy.legacy.datamodel.Loan
import com.ivy.legacy.datamodel.temp.toDomain
import com.ivy.frp.action.FPAction
import com.ivy.frp.action.thenMap
import com.ivy.frp.then
import com.ivy.data.db.dao.read.LoanDao
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import javax.inject.Inject

class LoansAct @Inject constructor(
    private val loanDao: LoanDao
) : FPAction<Unit, ImmutableList<Loan>>() {
    override suspend fun Unit.compose(): suspend () -> ImmutableList<Loan> = suspend {
        loanDao.findAll()
    } thenMap { it.toDomain() } then { it.toImmutableList() }
}
