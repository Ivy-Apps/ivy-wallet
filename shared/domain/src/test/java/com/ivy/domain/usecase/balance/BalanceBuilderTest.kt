package com.ivy.domain.usecase.balance

import com.ivy.data.model.primitive.AssetCode
import com.ivy.data.model.primitive.NonNegativeInt
import com.ivy.data.model.primitive.NonZeroDouble
import com.ivy.domain.model.StatSummary
import com.ivy.domain.usecase.BalanceBuilder
import com.ivy.domain.usecase.account.AccountStats
import com.ivy.domain.usecase.stat.StatSummaryBuilderTest
import io.kotest.matchers.shouldBe
import org.junit.Test

class BalanceBuilderTest {

    enum class ValuesTestCase(
        val values: AccountStats,
        val expected: Map<AssetCode, NonZeroDouble>,
    ) {
        Empty(
            values = AccountStats(
                income = StatSummary(trnCount = NonNegativeInt.Zero, values = StatSummaryBuilderTest.ValuesTestCase.Empty.expected.values),
                transfersIn = StatSummary(trnCount = NonNegativeInt.Zero, values = StatSummaryBuilderTest.ValuesTestCase.Empty.expected.values),
                expense = StatSummary(trnCount = NonNegativeInt.Zero, values = StatSummaryBuilderTest.ValuesTestCase.Empty.expected.values),
                transfersOut = StatSummary(trnCount = NonNegativeInt.Zero, values = StatSummaryBuilderTest.ValuesTestCase.Empty.expected.values),
            ),
            expected = emptyMap()
        ),
    }

    @Test
    fun `when stats are empty`() {
        // given
        val testCase = ValuesTestCase.Empty
        val balanceBuilder = BalanceBuilder()

        // when
        val stats = testCase.values

        balanceBuilder.processDeposits(incomes = stats.income.values, transfersIn = stats.transfersIn.values)
        balanceBuilder.processWithdrawals(expenses = stats.expense.values, transfersOut = stats.transfersOut.values)
        val balance = balanceBuilder.build()

        // then
        balance shouldBe testCase.expected
    }
}