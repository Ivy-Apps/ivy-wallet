package ivy.automate.base

import arrow.core.Either

fun <A, B> Either<A, B>.getOrThrow(): B {
    return fold(
        ifLeft = { throw IvyError(it.toString()) },
        ifRight = { it }
    )
}