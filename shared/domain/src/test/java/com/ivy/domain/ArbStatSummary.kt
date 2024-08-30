package com.ivy.domain

import com.ivy.data.model.primitive.NonNegativeInt
import com.ivy.data.model.testing.assetCode
import com.ivy.data.model.testing.value
import com.ivy.domain.model.StatSummary
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.nonNegativeInt

fun Arb.Companion.statSummary(): Arb<StatSummary> {
    return Arb.bind(
        Arb.nonNegativeInt(),
        Arb.map(keyArb = assetCode(), valueArb = Arb.value().map { it.amount })
    ) { _, trns ->
        val nonNegativeInt =
            if (trns.isNotEmpty()) {
                NonNegativeInt.unsafe(trns.size)
            } else {
                NonNegativeInt.Zero
            }
        StatSummary(nonNegativeInt, trns)
    }
}