package com.ivy.core.domain.action.transaction.transfer

import com.ivy.core.domain.action.Action
import com.ivy.core.domain.action.transaction.WriteTrnsBatchAct
import javax.inject.Inject

class WriteTransferAct @Inject constructor(
    private val writeTrnsBatchAct: WriteTrnsBatchAct
) : Action<ModifyTransfer, Unit>() {
    override suspend fun ModifyTransfer.willDo() {
        TODO("Not yet implemented")
    }
}
