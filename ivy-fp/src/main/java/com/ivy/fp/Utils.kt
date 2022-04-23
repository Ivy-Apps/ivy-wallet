package com.ivy.fp

suspend fun <A> List<A>.sumOfSuspend(
    selector: suspend (A) -> Double
): Double {
    var sum = 0.0
    for (item in this) {
        sum += selector(item)
    }
    return sum
}