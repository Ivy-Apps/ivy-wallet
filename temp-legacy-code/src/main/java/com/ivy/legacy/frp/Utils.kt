package com.ivy.frp

suspend fun <A> List<A>.sumOfSuspend(
    selector: suspend (A) -> Double
): Double {
    var sum = 0.0
    for (item in this) {
        sum += selector(item)
    }
    return sum
}

suspend fun <A> Collection<A>.filterSuspend(
    predicate: suspend (A) -> Boolean
): Collection<A> {
    return this.filter { a ->
        predicate(a)
    }
}