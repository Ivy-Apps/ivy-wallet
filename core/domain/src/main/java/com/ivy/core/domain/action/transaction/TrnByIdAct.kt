package com.ivy.core.domain.action.transaction

import com.ivy.core.domain.action.Action
import com.ivy.data.transaction.Transaction
import java.util.*
import javax.inject.Inject

class TrnByIdAct @Inject constructor(
    private val trnsByQueryAct: TrnsByQueryAct,
) : Action<UUID, Transaction?>() {
    override suspend fun UUID.willDo(): Transaction? =
        trnsByQueryAct(TrnQuery.ById(this)).firstOrNull()
}