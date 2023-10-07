package ivy.automate.base

import arrow.core.Either
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@DslMarker
annotation class IvyDsl

suspend fun <A> catchIO(
    block: suspend () -> A
): Either<Throwable, A> = Either.catch {
    withContext(Dispatchers.IO) {
        block()
    }
}

