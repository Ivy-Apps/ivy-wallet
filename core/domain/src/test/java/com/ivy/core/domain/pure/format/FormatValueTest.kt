package com.ivy.core.domain.pure.format

import com.ivy.data.Value
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.boolean
import io.kotest.property.checkAll

class FormatValueTest : StringSpec({
    "format 5 USD into 5 USD" {
        checkAll(Arb.boolean()) { shorten ->
            val res = format(
                value = Value(5.0, "USD"),
                shortenFiat = shorten
            )

            res shouldBe ValueUi(
                amount = "5",
                currency = "USD"
            )
        }
    }

    "format 0.25 EUR to 0.25 EUR" {
        checkAll(Arb.boolean()) { shorten ->
            val res = format(
                value = Value(0.25, "EUR"),
                shortenFiat = shorten
            )

            res shouldBe ValueUi(
                amount = "0.25",
                currency = "EUR"
            )
        }
    }

    "format shortened 129,765.68 GBP to 129.77k GBP" {
        val res = format(
            value = Value(129_765.68, "GBP"),
            shortenFiat = true
        )

        res shouldBe ValueUi(
            amount = "129.77k",
            currency = "GBP"
        )
    }

    "format not-shortened 23,501.29 BGN to 23,501.29 BGN" {
        val res = format(
            value = Value(23_501.29, "BGN"),
            shortenFiat = false
        )

        res shouldBe ValueUi(
            amount = "23,501.29",
            currency = "BGN"
        )
    }

    "format 3.00065 BTC to 3.00065 BTC" {
        checkAll(Arb.boolean()) { shorten ->
            val res = format(
                value = Value(3.00065, "BTC"),
                shortenFiat = shorten
            )

            res shouldBe ValueUi(
                amount = "3.00065",
                currency = "BTC"
            )
        }
    }

    "format 3,054.071 ADA to 3,054.071 ADA" {
        checkAll(Arb.boolean()) { shorten ->
            val res = format(
                value = Value(amount = 3_054.071, "ADA"),
                shortenFiat = shorten
            )

            res shouldBe ValueUi(
                amount = "3,054.071",
                currency = "ADA"
            )
        }
    }
})