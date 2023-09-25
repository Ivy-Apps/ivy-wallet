package com.ivy.frp.action

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Deprecated("Legacy code. Don't use it, please.")
abstract class FPAction<I, O> : Action<I, O>() {
    @Deprecated("Legacy code. Don't use it, please.")
    protected abstract suspend fun I.compose(): (suspend () -> O)

    @Deprecated("Legacy code. Don't use it, please.")
    protected open fun dispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Deprecated("Legacy code. Don't use it, please.")
    override suspend fun I.willDo(): O {
        return withContext(dispatcher()) {
            compose().invoke()
        }
    }
}