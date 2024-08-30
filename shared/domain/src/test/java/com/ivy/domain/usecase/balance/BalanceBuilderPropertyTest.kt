package com.ivy.domain.usecase.balance

import com.ivy.base.TestDispatchersProvider
import com.ivy.data.model.getFromAccount
import com.ivy.data.model.getToAccount
import com.ivy.data.model.primitive.AssetCode
import com.ivy.data.model.testing.ModelFixtures
import com.ivy.data.model.testing.transaction
import com.ivy.domain.statSummary
import com.ivy.domain.usecase.BalanceBuilder
import com.ivy.domain.usecase.account.AccountStatsUseCase
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.list
import io.kotest.property.checkAll
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class BalanceBuilderPropertyTest {

    private lateinit var statsUseCase: AccountStatsUseCase

    @Before
    fun setup() {
        statsUseCase = AccountStatsUseCase(
            dispatchers = TestDispatchersProvider,
            accountRepository = mockk(),
            exchangeUseCase = mockk(),
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
            val stats = statsUseCase.calculate(account, trns)

            // then
            BalanceBuilder().run {
                processDeposits(incomes = stats.income.values, transfersIn = stats.transfersIn.values)
                processWithdrawals(expenses = stats.expense.values, transfersOut = stats.transfersOut.values)
                build()
            } shouldBe emptyMap()
        }
    }

    @Test
    fun `property - check all BGP currency`() = runTest {
        // given
        val incomes = Arb.statSummary()
        val trnsIn = Arb.statSummary()
        val expenses = Arb.statSummary()
        val trnsOut = Arb.statSummary()

        checkAll(
            incomes,
            trnsIn,
            expenses,
            trnsOut
        ) { incomes, transfersIn, expenses, transferOut ->
            // when
            val depositBGP = incomes.values[AssetCode.GBP]?.value?.plus(
                transfersIn.values[AssetCode.GBP]?.value ?: 0.0
            )
            val withdrawalsBGP = expenses.values[AssetCode.GBP]?.value?.plus(
                transferOut.values[AssetCode.GBP]?.value ?: 0.0
            )
            val totalBGP = depositBGP?.minus(withdrawalsBGP ?: 0.0)

            BalanceBuilder().run {
                processDeposits(incomes = incomes.values, transfersIn = transfersIn.values)
                processWithdrawals(expenses = expenses.values, transfersOut = transferOut.values)
                build()
            }[AssetCode.GBP] shouldBe totalBGP
        }
    }

    @Test
    fun `property - check all EUR currency`() = runTest {
        // given
        val incomes = Arb.statSummary()
        val trnsIn = Arb.statSummary()
        val expenses = Arb.statSummary()
        val trnsOut = Arb.statSummary()

        checkAll(
            incomes,
            trnsIn,
            expenses,
            trnsOut
        ) { incomes, transfersIn, expenses, transferOut ->
            // when
            val depositEUR = incomes.values[AssetCode.EUR]?.value?.plus(transfersIn.values[AssetCode.EUR]?.value ?: 0.0)
            val withdrawalsEUR = expenses.values[AssetCode.EUR]?.value?.plus(
                transferOut.values[AssetCode.EUR]?.value
                    ?: 0.0
            )
            val totalEUR = depositEUR?.minus(withdrawalsEUR ?: 0.0)

            BalanceBuilder().run {
                processDeposits(incomes = incomes.values, transfersIn = transfersIn.values)
                processWithdrawals(expenses = expenses.values, transfersOut = transferOut.values)
                build()
            }[AssetCode.EUR] shouldBe totalEUR
        }
    }

    @Test
    fun `property - check all USD currency`() = runTest {
        // given
        val incomes = Arb.statSummary()
        val trnsIn = Arb.statSummary()
        val expenses = Arb.statSummary()
        val trnsOut = Arb.statSummary()

        checkAll(
            incomes,
            trnsIn,
            expenses,
            trnsOut
        ) { incomes,
            transfersIn,
            expenses,
            transferOut ->
            // when
            val depositUSD = incomes.values[AssetCode.USD]?.value?.plus(transfersIn.values[AssetCode.USD]?.value ?: 0.0)
            val withdrawalsUSD = expenses.values[AssetCode.USD]?.value?.plus(
                transferOut.values[AssetCode.USD]?.value
                    ?: 0.0
            )
            val totalUSD = depositUSD?.minus(withdrawalsUSD ?: 0.0)

            BalanceBuilder().run {
                processDeposits(incomes = incomes.values, transfersIn = transfersIn.values)
                processWithdrawals(expenses = expenses.values, transfersOut = transferOut.values)
                build()
            }[AssetCode.USD] shouldBe totalUSD
        }
    }
}