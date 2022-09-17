package com.ivy.core.domain.pure.account

import com.ivy.core.domain.pure.dummy.dummyAcc
import com.ivy.data.Value
import com.ivy.data.account.Account
import com.ivy.data.transaction.Transaction
import com.ivy.data.transaction.TrnPurpose
import com.ivy.data.transaction.TrnTime
import com.ivy.data.transaction.TrnType
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.doubles.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNot
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.beBlank
import io.kotest.matchers.types.instanceOf
import io.kotest.property.Arb
import io.kotest.property.arbitrary.boolean
import io.kotest.property.checkAll

class AdjustBalanceTest : StringSpec({
    // region Helpers
    fun Transaction?.assert(
        amount: Double,
        trnType: TrnType,
        acc: Account,
    ) {
        this shouldNotBe null
        with(this!!) {
            value shouldBe Value(amount, acc.currency)
            type shouldBe trnType
            purpose shouldBe TrnPurpose.AdjustBalance
            time shouldBe instanceOf<TrnTime.Actual>()
            title shouldNot beBlank()
            account shouldBe acc
        }
    }
    // endregion

    "adjust 50 USD to 40 USD" {
        checkAll(Arb.boolean()) { hiddenTrn ->
            val acc = dummyAcc(currency = "USD")

            val adjustTrn = adjustBalanceTrn(
                account = acc,
                currentBalance = 50.0,
                desiredBalance = 40.0,
                hiddenTrn = hiddenTrn
            )

            adjustTrn.assert(
                amount = 10.0,
                trnType = TrnType.Expense,
                acc = acc
            )
        }
    }

    "adjust 33.67 EUR to 100 EUR" {
        checkAll(Arb.boolean()) { hiddenTrn ->
            val acc = dummyAcc(currency = "EUR")

            val adjustTrn = adjustBalanceTrn(
                account = acc,
                currentBalance = 33.67,
                desiredBalance = 100.0,
                hiddenTrn = hiddenTrn
            )

            adjustTrn.assert(
                amount = 66.33,
                trnType = TrnType.Income,
                acc = acc
            )
        }
    }

    "adjust insignificant" {
        val acc = dummyAcc(currency = "USD")

        val res = adjustBalanceTrn(
            account = acc,
            currentBalance = 1_023.55,
            desiredBalance = 1_023.555,
            hiddenTrn = false
        )

        res shouldBe null
    }

    "adjust 0.00345 BTC to 0.00346 BTC" {
        val acc = dummyAcc(currency = "BTC")

        val res = adjustBalanceTrn(
            account = acc,
            currentBalance = .00345,
            desiredBalance = .00346,
            hiddenTrn = true
        )

        res shouldNotBe null
        res!!.value.amount shouldBeGreaterThan 0.0
        res.type shouldBe TrnType.Income
    }
})