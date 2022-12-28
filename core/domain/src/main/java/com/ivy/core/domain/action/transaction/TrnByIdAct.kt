package com.ivy.core.domain.action.transaction

import com.ivy.core.domain.action.Action
import com.ivy.data.transaction.Transaction
import kotlinx.coroutines.flow.first
import java.util.*
import javax.inject.Inject

class TrnByIdAct @Inject constructor(
    private val trnsFlow: TrnsFlow,
) : Action<UUID, Transaction?>() {
    override suspend fun UUID.willDo(): Transaction? =
        trnsFlow(TrnQuery.ById(this))
            .first().firstOrNull()
}