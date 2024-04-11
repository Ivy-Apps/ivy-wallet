package com.ivy.data.model.testing

import arrow.core.Some
import com.ivy.data.model.primitive.AssetCode
import com.ivy.data.model.primitive.PositiveDouble
import io.kotest.property.Arb
import io.kotest.property.forAll
import kotlinx.coroutines.test.runTest
import org.junit.Test

class ArbValueTest {
    @Test
    fun `generates arb values`() = runTest {
        forAll(Arb.value()) { value ->
            value.amount.value <= MaxArbValueAllowed
        }
    }

    @Test
    fun `arb value respects params`() = runTest {
        forAll(
            Arb.value(
                amount = Some(PositiveDouble.unsafe(3.14)),
                asset = Some(AssetCode.EUR),
            )
        ) { value ->
            value.amount.value == 3.14 && value.asset == AssetCode.EUR
        }
    }
}