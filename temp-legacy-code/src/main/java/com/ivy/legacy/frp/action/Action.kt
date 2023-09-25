package com.ivy.frp.action

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext

@Deprecated("Legacy code. Don't use it, please.")
abstract class Action<in I, out O> {
    @Deprecated("Legacy code. Don't use it, please.")
    abstract suspend fun I.willDo(): O

    suspend operator fun invoke(input: I): O {
        return input.willDo()
    }

    protected suspend fun <T> io(action: suspend () -> T): T = withContext(Dispatchers.IO) {
        return@withContext action()
    }

    protected suspend fun <T> asyncIo(action: suspend () -> T): Deferred<T> =
        withContext(Dispatchers.IO) {
            return@withContext this.async { action() }
        }

    protected suspend fun <T> computation(action: suspend () -> T): T =
        withContext(Dispatchers.Default) {
            return@withContext action()
        }

    protected suspend fun <T> ui(action: suspend () -> T): T =
        withContext(Dispatchers.Main) {
            return@withContext action()
        }
}