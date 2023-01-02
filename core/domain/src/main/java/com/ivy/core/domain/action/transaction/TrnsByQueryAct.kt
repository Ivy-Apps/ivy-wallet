package com.ivy.core.domain.action.transaction

import com.ivy.core.domain.action.Action
import com.ivy.data.transaction.Transaction
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class TrnsByQueryAct @Inject constructor(
    private val trnsFlow: TrnsFlow,
) : Action<TrnQuery, List<Transaction>>() {
    override suspend fun TrnQuery.willDo(): List<Transaction> =
        trnsFlow(this).first()

}