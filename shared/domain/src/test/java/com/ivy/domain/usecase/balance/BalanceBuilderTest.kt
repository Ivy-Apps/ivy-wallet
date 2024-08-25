package com.ivy.domain.usecase.balance

import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import com.ivy.data.model.primitive.AssetCode
import com.ivy.data.model.primitive.AssetCode.Companion.EUR
import com.ivy.data.model.primitive.AssetCode.Companion.GBP
import com.ivy.data.model.primitive.AssetCode.Companion.USD
import com.ivy.data.model.primitive.NonNegativeInt
import com.ivy.data.model.primitive.NonZeroDouble
import com.ivy.data.model.primitive.PositiveDouble
import com.ivy.domain.model.StatSummary
import com.ivy.domain.usecase.BalanceBuilder
import com.ivy.domain.usecase.account.AccountStats
import io.kotest.matchers.shouldBe
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(TestParameterInjector::class)
class BalanceBuilderTest {

    enum class ValuesTestCase(
        val values: AccountStats,
        val expected: Map<AssetCode, NonZeroDouble>,
    ) {
        Empty(
            values = AccountStats(
                income = StatSummary(
                    trnCount = NonNegativeInt.Zero,
                    values = StatSummary.Zero.values
                ),
                transfersIn = StatSummary(
                    trnCount = NonNegativeInt.Zero,
                    values = StatSummary.Zero.values
                ),
                expense = StatSummary(
                    trnCount = NonNegativeInt.Zero,
                    values = StatSummary.Zero.values
                ),
                transfersOut = StatSummary(
                    trnCount = NonNegativeInt.Zero,
                    values = StatSummary.Zero.values
                ),
            ),
            expected = emptyMap()
        ),
        OneDepositFromIncome(
            values = AccountStats(
                income = StatSummary(
                    trnCount = count(1),
                    values = mapOf(EUR to positiveDouble(1.0))
                ),
                transfersIn = StatSummary(
                    trnCount = NonNegativeInt.Zero,
                    values = StatSummary.Zero.values
                ),
                expense = StatSummary(
                    trnCount = NonNegativeInt.Zero,
                    values = StatSummary.Zero.values
                ),
                transfersOut = StatSummary(
                    trnCount = NonNegativeInt.Zero,
                    values = StatSummary.Zero.values
                ),
            ),
            expected = mapOf(
                EUR to nonZeroDouble(1.0)
            )
        ),
        TwoInDiffCurrencyDepositsFromIncome(
            values = AccountStats(
                income = StatSummary(
                    trnCount = count(2),
                    values = mapOf(
                        EUR to positiveDouble(3.14),
                        USD to positiveDouble(42.0)
                    )
                ),
                transfersIn = StatSummary(
                    trnCount = NonNegativeInt.Zero,
                    values = StatSummary.Zero.values
                ),
                expense = StatSummary(
                    trnCount = NonNegativeInt.Zero,
                    values = StatSummary.Zero.values
                ),
                transfersOut = StatSummary(
                    trnCount = NonNegativeInt.Zero,
                    values = StatSummary.Zero.values
                ),
            ),
            expected = mapOf(
                EUR to nonZeroDouble(3.14),
                USD to nonZeroDouble(42.0)
            )
        ),
        TwoInSameCurrencyDepositsFromIncomeAndTransfersIn(
            values = AccountStats(
                income = StatSummary(
                    trnCount = count(1),
                    values = mapOf(EUR to positiveDouble(6.0))
                ),
                transfersIn = StatSummary(
                    trnCount = count(1),
                    values = mapOf(EUR to positiveDouble(4.0))
                ),
                expense = StatSummary(
                    trnCount = NonNegativeInt.Zero,
                    values = StatSummary.Zero.values
                ),
                transfersOut = StatSummary(
                    trnCount = NonNegativeInt.Zero,
                    values = StatSummary.Zero.values
                ),
            ),
            expected = mapOf(
                EUR to nonZeroDouble(10.0)
            )
        ),
        TwoInDiffCurrencyDepositsFromIncomeAndTransfersIn(
            values = AccountStats(
                income = StatSummary(
                    trnCount = count(2),
                    values = mapOf(
                        EUR to positiveDouble(3.14)
                    )
                ),
                transfersIn = StatSummary(
                    trnCount = count(1),
                    values = mapOf(USD to positiveDouble(50.0))
                ),
                expense = StatSummary(
                    trnCount = NonNegativeInt.Zero,
                    values = StatSummary.Zero.values
                ),
                transfersOut = StatSummary(
                    trnCount = NonNegativeInt.Zero,
                    values = StatSummary.Zero.values
                ),
            ),
            expected = mapOf(
                EUR to nonZeroDouble(3.14),
                USD to nonZeroDouble(50.0)
            )
        ),
        TwoDepositsInDiffCurrencyIncomeAndOneTransferIn(
            values = AccountStats(
                income = StatSummary(
                    trnCount = count(2),
                    values = mapOf(
                        USD to positiveDouble(50.0),
                        EUR to positiveDouble(3.14)
                    )
                ),
                transfersIn = StatSummary(
                    trnCount = count(1),
                    values = mapOf(
                        GBP to positiveDouble(0.5)
                    )
                ),
                expense = StatSummary(
                    trnCount = NonNegativeInt.Zero,
                    values = StatSummary.Zero.values
                ),
                transfersOut = StatSummary(
                    trnCount = NonNegativeInt.Zero,
                    values = StatSummary.Zero.values
                ),
            ),
            expected = mapOf(
                EUR to nonZeroDouble(3.14),
                USD to nonZeroDouble(50.0),
                GBP to nonZeroDouble(0.5)
            )
        ),
        TwoWithdrawalsCurrencyExpensesAndTransferOut(
            values = AccountStats(
                income = StatSummary(
                    trnCount = NonNegativeInt.Zero,
                    values = emptyMap()
                ),
                transfersIn = StatSummary(
                    trnCount = NonNegativeInt.Zero,
                    values = emptyMap()
                ),
                expense = StatSummary(
                    trnCount = count(1),
                    values = mapOf(
                        USD to positiveDouble(50.0)
                    )
                ),
                transfersOut = StatSummary(
                    trnCount = count(1),
                    values = mapOf(
                        USD to positiveDouble(0.5)
                    )
                ),
            ),
            expected = mapOf(
                USD to nonZeroDouble(-50.5),
            )
        ),
        TwoDepositsAndTwoWithdrawals(
            values = AccountStats(
                income = StatSummary(
                    trnCount = count(1),
                    values = mapOf(
                        USD to positiveDouble(50.0)
                    )
                ),
                transfersIn = StatSummary(
                    trnCount = count(1),
                    values = mapOf(
                        USD to positiveDouble(0.5)
                    )
                ),
                expense = StatSummary(
                    trnCount = count(1),
                    values = mapOf(
                        USD to positiveDouble(50.0)
                    )
                ),
                transfersOut = StatSummary(
                    trnCount = count(1),
                    values = mapOf(
                        USD to positiveDouble(0.5)
                    )
                ),
            ),
            expected = emptyMap()
        ),
        TwoDepositsAndTwoExpensesInDiffCurrencyAndTwoTransferOutInSame(
            values = AccountStats(
                income = StatSummary(
                    trnCount = count(1),
                    values = mapOf(
                        USD to positiveDouble(50.0)
                    )
                ),
                transfersIn = StatSummary(
                    trnCount = count(1),
                    values = mapOf(
                        USD to positiveDouble(0.5)
                    )
                ),
                expense = StatSummary(
                    trnCount = count(3),
                    values = mapOf(
                        USD to positiveDouble(50.0),
                        EUR to positiveDouble(40.0),
                        GBP to positiveDouble(3.15),
                    )
                ),
                transfersOut = StatSummary(
                    trnCount = count(1),
                    values = mapOf(
                        GBP to positiveDouble(4.45)
                    )
                ),
            ),
            expected = mapOf(
                USD to nonZeroDouble(0.5),
                EUR to nonZeroDouble(-40.0),
                GBP to nonZeroDouble(-7.60)
            )
        ),
        WhenBalanceIsZeroInAtLeastOneCurrency(
            values = AccountStats(
                income = StatSummary(
                    trnCount = count(1),
                    values = mapOf(
                        USD to positiveDouble(50.0)
                    )
                ),
                transfersIn = StatSummary(
                    trnCount = count(2),
                    values = mapOf(
                        USD to positiveDouble(50.0),
                        EUR to positiveDouble(80.0)
                    )
                ),
                expense = StatSummary(
                    trnCount = count(2),
                    values = mapOf(
                        USD to positiveDouble(50.0),
                        EUR to positiveDouble(40.0)
                    )
                ),
                transfersOut = StatSummary(
                    trnCount = count(1),
                    values = mapOf(
                        USD to positiveDouble(50.0),
                    )
                ),
            ),
            expected = mapOf(
                EUR to nonZeroDouble(40.0)
            )
        )
    }

    @Test
    fun `builds balance`(
        @TestParameter testCase: ValuesTestCase
    ) {
        // given
        val stats = testCase.values
        val balanceBuilder = BalanceBuilder()

        // when
        balanceBuilder.processDeposits(
            incomes = stats.income.values,
            transfersIn = stats.transfersIn.values
        )
        balanceBuilder.processWithdrawals(
            expenses = stats.expense.values,
            transfersOut = stats.transfersOut.values
        )
        val balance = balanceBuilder.build()

        // then
        balance shouldBe testCase.expected
    }

    companion object {

        private fun count(count: Int): NonNegativeInt = NonNegativeInt.unsafe(count)

        private fun positiveDouble(amount: Double): PositiveDouble = PositiveDouble.unsafe(amount)

        private fun nonZeroDouble(amount: Double): NonZeroDouble = NonZeroDouble.unsafe(amount)
    }
}