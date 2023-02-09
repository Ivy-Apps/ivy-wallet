package com.ivy.backup.base

import arrow.core.Either
import com.ivy.core.data.sync.IvyWalletData
import com.ivy.core.domain.action.Action
import com.ivy.core.domain.api.data.ActionError
import javax.inject.Inject

class WriteIvyWalletDataAct @Inject constructor(

) : Action<IvyWalletData, Either<ActionError, Unit>>() {
    override suspend fun action(input: IvyWalletData): Either<ActionError, Unit> {
        TODO("Not yet implemented")
    }
}