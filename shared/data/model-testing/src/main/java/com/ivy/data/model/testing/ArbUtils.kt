package com.ivy.data.model.testing

import com.ivy.data.model.primitive.ColorInt
import com.ivy.data.model.primitive.IconAsset
import com.ivy.data.model.primitive.NotBlankTrimmedString
import com.ivy.data.model.primitive.PositiveDouble
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.int
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

fun <A> Arb.Companion.or(a: Arb<A>, b: Arb<A>): Arb<A> = arbitrary {
    if (Arb.boolean().bind()) {
        a.bind()
    } else {
        b.bind()
    }
}

fun <A> Arb.Companion.ofValue(a: A): Arb<A> = arbitrary { a }

fun Arb.Companion.positiveDoubleExact(
    max: Double = Double.MAX_VALUE,
): Arb<PositiveDouble> = Arb.positiveDouble(
    max = max,
    includeNonFiniteEdgeCases = false,
).map(PositiveDouble::unsafe)

fun Arb.Companion.colorInt(): Arb<ColorInt> = Arb.int().map(::ColorInt)

fun Arb.Companion.iconAsset(): Arb<IconAsset> = Arb.notBlankTrimmedString().map {
    IconAsset.unsafe(it.value.filter { c -> !c.isWhitespace() })
}