package com.ivy.frp

@Deprecated("Legacy code. Don't use it, please.")
suspend fun <A> List<A>.sumOfSuspend(
    selector: suspend (A) -> Double
): Double {
    var sum = 0.0
    for (item in this) {
        sum += selector(item)
    }
    return sum
}

@Deprecated("Legacy code. Don't use it, please.")
suspend fun <A> Collection<A>.filterSuspend(
    predicate: suspend (A) -> Boolean
): Collection<A> {
    return this.filter { a ->
        predicate(a)
    }
}