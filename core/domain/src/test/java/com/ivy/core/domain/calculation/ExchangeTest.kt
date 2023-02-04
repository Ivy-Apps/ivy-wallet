package com.ivy.core.domain.calculation

import arrow.core.None
import arrow.core.Some
import com.ivy.core.data.common.AssetCode
import com.ivy.core.data.common.toNonNegativeUnsafe
import io.kotest.assertions.arrow.core.shouldBeSome
import io.kotest.core.spec.style.FreeSpec
import io.kotest.data.row
import io.kotest.datatest.withData
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
                    from = AssetCode.fromStringUnsafe(from.second),
                    to = AssetCode.fromStringUnsafe(to.second),
                    amount = from.first.toNonNegativeUnsafe()
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
                    from = asset1,
                    to = asset2,
                    amount = original
                )
                exchanged.isDefined() shouldBe true

                exchange(
                    from = asset2,
                    to = asset1,
                    amount = (exchanged as Some).value
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
})