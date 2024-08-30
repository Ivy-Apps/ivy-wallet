package com.ivy.domain.usecase.balance

import com.ivy.base.TestDispatchersProvider
import com.ivy.data.model.getFromAccount
import com.ivy.data.model.getToAccount
import com.ivy.data.model.testing.ModelFixtures
import com.ivy.data.model.testing.transaction
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

    private lateinit var useCase: AccountStatsUseCase

    @Before
    fun setup() {
        useCase = AccountStatsUseCase(
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
            val stats = useCase.calculate(account, trns)

            // then
            BalanceBuilder().run {
                processDeposits(incomes = stats.income.values, transfersIn = stats.transfersIn.values)
                processWithdrawals(expenses = stats.income.values, transfersOut = stats.transfersOut.values)
                build()
            } shouldBe emptyMap()
        }
    }
}