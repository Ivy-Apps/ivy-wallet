package com.ivy.core.domain.action.transaction

import com.ivy.core.domain.action.Action
import com.ivy.data.transaction.Transaction
import java.util.*
import javax.inject.Inject

class TrnByIdAct @Inject constructor(
    private val trnsByQueryAct: TrnsByQueryAct,
) : Action<UUID, Transaction?>() {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override suspend fun action(trnId: UUID): Transaction? =
        trnsByQueryAct(TrnQuery.ById(trnId)).firstOrNull()
}