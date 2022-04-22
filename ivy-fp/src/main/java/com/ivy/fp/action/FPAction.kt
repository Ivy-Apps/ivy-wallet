package com.ivy.fp.action

abstract class FPAction<I, O> : Action<I, O>() {
    protected abstract suspend fun I.compose(): (suspend () -> O)

    override suspend fun I.willDo(): O {
        return compose().invoke()
    }
}