package com.ivy.frp.action

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class FPAction<I, O> : Action<I, O>() {
    protected abstract suspend fun I.compose(): (suspend () -> O)

    protected open fun dispatcher(): CoroutineDispatcher = Dispatchers.IO

    override suspend fun I.willDo(): O {
        return withContext(dispatcher()) {
            compose().invoke()
        }
    }
}