package com.ivy.wallet.domain.action.budget

import com.ivy.frp.action.FPAction
import com.ivy.frp.action.thenMap
import com.ivy.wallet.domain.data.core.Budget
import com.ivy.wallet.io.persistence.dao.BudgetDao
import javax.inject.Inject

class BudgetsAct @Inject constructor(
    private val budgetDao: BudgetDao
) : FPAction<Unit, List<Budget>>() {
    override suspend fun Unit.compose(): suspend () -> List<Budget> = suspend {
        budgetDao.findAll()
    } thenMap { it.toDomain() }
}