package com.ivy.core.domain.action

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class Action<in I, out O> {
    abstract suspend fun I.willDo(): O

    protected open fun dispatcher(): CoroutineDispatcher = Dispatchers.IO

    suspend operator fun invoke(input: I): O = withContext(dispatcher()) {
        input.willDo()
    }
}