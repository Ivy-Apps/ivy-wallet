package com.ivy.core.domain.calculation

import arrow.core.None
import arrow.core.Some
import com.ivy.core.data.common.AssetCode
import com.ivy.core.data.common.toNonNegativeUnsafe
import io.kotest.assertions.arrow.core.shouldBeSome
import io.kotest.core.spec.style.FreeSpec
import io.kotest.data.row
import io.kotest.datatest.withData
import io.kotest.matchers.doubles.shouldBeGreaterThan
import io.kotest.matchers.doubles.shouldBeLessThan
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.positiveInt
import io.kotest.property.checkAll

class ExchangeTest : FreeSpec({
    val rates = exchangeRates(
        base = "BGN",
        rates = mapOf(
            "EUR" to 0.5114,
            "USD" to 0.5550,
            "GBP" to 0.4588,
            "BTC" to 0.000024,
        )
    )

    "exchanges valid" - {
        with(rates) {
            withData(
                nameFn = { (from, to) ->
                    "${from.first} ${from.second} should be ${to.first} ${to.second}"
                },
                row(
                    1.0 to "EUR", 1.96 to "BGN"
                ),
                row(
                    100.0 to "EUR", 195.54 to "BGN"
                ),
                row(
                    10.0 to "USD", 9.21 to "EUR",
                ),
                row(
                    5.0 to "USD", 4.13 to "GBP",
                ),
                row(
                    1.0 to "BTC", 41_666.67 to "BGN"
                )
            ) { (from, to) ->
                val res = exchange(
                    amount = from.first.toNonNegativeUnsafe(),
                    from = AssetCode.fromStringUnsafe(from.second),
                    to = AssetCode.fromStringUnsafe(to.second)
                )

                res.map { it.value.round() } shouldBeSome to.first.round()
            }
        }
    }

    "exchange cycle property" {
        val arbValidAsset = arbitrary {
            rates.rates.keys.random()
        }

        // PROPERTY: 1 BGN -> 0.51 EUR -> 1 BGN
        checkAll(
            arbValidAsset, arbValidAsset, Arb.positiveInt()
        ) { asset1, asset2, randomAmount ->
            with(rates) {
                val original = randomAmount.toDouble().toNonNegativeUnsafe()
                val exchanged = exchange(
                    amount = original,
                    from = asset1,
                    to = asset2
                )
                exchanged.isDefined() shouldBe true

                exchange(
                    amount = (exchanged as Some).value,
                    from = asset2,
                    to = asset1
                ).map { it.value.round() } shouldBeSome original.value.round()
            }
        }
    }

    "exchange missing rate property" {
        val arbValidAsset = arbitrary {
            rates.rates.keys.random()
        }

        val arbInput = arbitrary {
            when (Arb.int(1..3).bind()) {
                1 -> {
                    // from valid, to invalid
                    arbValidAsset.bind() to Arb.assetCode().bind()
                }

                2 -> {
                    // from invalid, to valid
                    Arb.assetCode().bind() to arbValidAsset.bind()
                }

                else -> {
                    // both invalid
                    Arb.assetCode().bind() to Arb.assetCode().bind()
                }
            }
        }

        // PROPERTY: Missing exchange rates returns None
        checkAll(
            arbInput.filter { it.first != it.second },
            Arb.positiveInt()
        ) { (from, to), amount ->
            with(rates) {
                exchange(
                    from = from,
                    to = to,
                    amount = amount.toDouble().toNonNegativeUnsafe(),
                ) shouldBe None
            }
        }
    }

    "EDGE CASE: Disappearing rate ~= 0" {
        // Arrange
        val smallRates = exchangeRates(
            base = "BGN",
            rates = mapOf(
                "y" to 0.00000000000000000000000000000000000000000000000000000000001,
                "x" to 1_000_000_000_000_000.0,
            )
        )

        // Act
        val res = smallRates.exchange(
            amount = 1.0.toNonNegativeUnsafe(),
            from = AssetCode.fromStringUnsafe("x"),
            to = AssetCode.fromStringUnsafe("y")
        )

        // Assert
        println(res)
        res.isDefined() shouldBe true
        val exchanged = (res as Some).value.value
        exchanged shouldBeLessThan 1.0
        exchanged shouldBeGreaterThan 0.0
        exchanged.round() shouldBe "0.00"
    }
})