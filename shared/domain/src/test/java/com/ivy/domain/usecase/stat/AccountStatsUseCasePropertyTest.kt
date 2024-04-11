package com.ivy.domain.usecase.stat

import arrow.core.Some
import com.ivy.base.TestDispatchersProvider
import com.ivy.data.model.Income
import com.ivy.data.model.Value
import com.ivy.data.model.getFromAccount
import com.ivy.data.model.getToAccount
import com.ivy.data.model.primitive.AssetCode
import com.ivy.data.model.primitive.NonNegativeInt
import com.ivy.data.model.primitive.PositiveDouble
import com.ivy.data.model.testing.ModelFixtures
import com.ivy.data.model.testing.income
import com.ivy.data.model.testing.transaction
import com.ivy.domain.model.AccountStats
import com.ivy.domain.model.StatSummary
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.list
import io.kotest.property.checkAll
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class AccountStatsUseCasePropertyTest {

    private lateinit var useCase: AccountStatsUseCase

    @Before
    fun setup() {
        useCase = AccountStatsUseCase(
            dispatchers = TestDispatchersProvider
        )
    }

    @Test
    fun `property - ignores irrelevant transactions`() = runTest {
        // given
        val account = ModelFixtures.AccountId
        val arbIrrelevantTransaction = Arb.transaction().filter { trn ->
            trn.getFromAccount() != account && trn.getToAccount() != account
        }

        checkAll(Arb.list(arbIrrelevantTransaction)) { trns ->
            // when
            val stats = useCase.calculate(account, trns)

            // then
            stats shouldBe AccountStats.Zero
        }
    }

    @Test
    fun `property - sums incomes in account`() = runTest {
        // given
        val account = ModelFixtures.AccountId
        val arbEurIncomes = Arb.list(
            gen = Arb.income(
                accountId = Some(account),
                asset = Some(AssetCode.EUR)
            ),
            range = 1..100
        )
        val arbUsdIncomes = Arb.list(
            gen = Arb.income(
                accountId = Some(account),
                asset = Some(AssetCode.USD)
            ),
            range = 1..100
        )

        checkAll(arbEurIncomes, arbUsdIncomes) { eurIncomes, usdIncomes ->
            // given
            val trns = (eurIncomes + usdIncomes).shuffled()
            val expectedEurIncome = eurIncomes.map(Income::value).sumSafe()
            val expectedUsdIncome = usdIncomes.map(Income::value).sumSafe()

            // when
            val stats = useCase.calculate(account, trns)

            // then
            stats shouldBe AccountStats.Zero.copy(
                income = StatSummary(
                    values = mapOf(
                        AssetCode.EUR to expectedEurIncome,
                        AssetCode.USD to expectedUsdIncome
                    ),
                    trnCount = NonNegativeInt.unsafe(eurIncomes.size + usdIncomes.size)
                )
            )
        }
    }

    private fun List<Value>.sumSafe(): PositiveDouble {
        if (isEmpty()) {
            error("Test setup error! The list must not be empty.")
        }
        var sum = 0.0
        for (value in this) {
            PositiveDouble.from(sum + value.amount.value)
                .onRight { newSum ->
                    sum = newSum.value
                }
        }
        return PositiveDouble.unsafe(sum)
    }
}