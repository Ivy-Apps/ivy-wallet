package com.ivy.core.action.calculate.transaction

import com.ivy.core.action.transaction.TrnsAct
import com.ivy.data.transaction.Transaction
import com.ivy.frp.action.FPAction
import javax.inject.Inject

class GroupByDateAct @Inject constructor(
    private val trnsAct: TrnsAct
) : FPAction<List<Transaction>, List<Any>>() {
    // TODO: Implement transactions grouping by date

    override suspend fun List<Transaction>.compose(): suspend () -> List<Any> {
        TODO("Not yet implemented")
    }
}