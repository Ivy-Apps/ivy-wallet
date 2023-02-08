package com.ivy.sync.action

import arrow.core.Either
import arrow.core.continuations.either
import com.ivy.core.domain.action.Action
import com.ivy.sync.data.SyncError
import javax.inject.Inject

class SyncAct @Inject constructor(

) : Action<Unit, Either<SyncError, Unit>>() {
    override suspend fun action(input: Unit): Either<SyncError, Unit> = either {

    }
}