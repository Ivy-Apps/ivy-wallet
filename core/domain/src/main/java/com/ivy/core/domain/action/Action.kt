package com.ivy.core.domain.action

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class Action<in Input, out Output> {
    protected abstract suspend fun action(input: Input): Output

    protected open fun dispatcher(): CoroutineDispatcher = Dispatchers.IO

    suspend operator fun invoke(input: Input): Output = withContext(dispatcher()) {
        action(input)
    }
}