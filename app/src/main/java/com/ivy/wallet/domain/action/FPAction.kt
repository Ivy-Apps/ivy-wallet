package com.ivy.wallet.domain.action

abstract class FPAction<I, O> : Action<I, O>() {
    protected abstract suspend fun I.recipe(): (suspend () -> O)

    override suspend fun I.willDo(): O {
        return recipe().invoke()
    }
}