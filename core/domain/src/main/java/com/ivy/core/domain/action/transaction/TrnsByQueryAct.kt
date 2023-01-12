package com.ivy.core.domain.action.transaction

import com.ivy.core.domain.action.Action
import com.ivy.data.transaction.Transaction
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class TrnsByQueryAct @Inject constructor(
    private val trnsFlow: TrnsFlow,
) : Action<TrnQuery, List<Transaction>>() {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override suspend fun action(query: TrnQuery): List<Transaction> =
        trnsFlow(query).first()

}