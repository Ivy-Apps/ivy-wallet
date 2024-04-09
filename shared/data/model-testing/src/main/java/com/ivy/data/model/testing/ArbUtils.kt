package com.ivy.data.model.testing

import com.ivy.data.model.primitive.NotBlankTrimmedString
import com.ivy.data.model.primitive.PositiveDouble
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.positiveDouble
import io.kotest.property.arbitrary.string

fun Arb.Companion.notBlankTrimmedString(): Arb<NotBlankTrimmedString> = Arb.string()
    .filter { it.trim().isNotBlank() }
    .map(NotBlankTrimmedString::unsafe)

fun <A> Arb.Companion.maybe(arb: Arb<A>): Arb<A?> = arbitrary {
    val shouldTake = Arb.boolean().bind()
    if (shouldTake) {
        arb.bind()
    } else {
        null
    }
}

fun Arb.Companion.positiveDoubleExact(): Arb<PositiveDouble> = Arb.positiveDouble()
    .filter { it.isFinite() }
    .map { PositiveDouble.unsafe(it) }