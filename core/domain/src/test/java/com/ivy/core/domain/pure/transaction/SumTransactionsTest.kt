package com.ivy.core.domain.pure.transaction

import arrow.core.nonEmptyListOf
import com.ivy.core.domain.pure.dummy.dummyTrn
import com.ivy.data.transaction.Transaction
import com.ivy.data.transaction.TrnType
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class SumTransactionsTest : StringSpec({
    class TestArg(val multiplier: Int)

    fun trnAmountMultiplied(
        trn: Transaction, arg: TestArg,
    ): Double = trn.value.amount * arg.multiplier

    @Suppress("RedundantSuspendModifier")
    suspend fun income(
        trn: Transaction, arg: TestArg
    ): Double = when (trn.type) {
        TrnType.Income -> trnAmountMultiplied(trn, arg)
        else -> 0.0
    }

    @Suppress("RedundantSuspendModifier")
    suspend fun expense(
        trn: Transaction, arg: TestArg
    ): Double = when (trn.type) {
        TrnType.Expense -> trnAmountMultiplied(trn, arg)
        else -> 0.0
    }

    @Suppress("RedundantSuspendModifier", "UNUSED_PARAMETER")
    suspend fun countIncome(
        trn: Transaction, arg: TestArg
    ): Double = when (trn.type) {
        TrnType.Income -> 1.0
        else -> 0.0
    }

    @Suppress("RedundantSuspendModifier", "UNUSED_PARAMETER")
    suspend fun countExpense(
        trn: Transaction, arg: TestArg
    ): Double = when (trn.type) {
        TrnType.Expense -> 1.0
        else -> 0.0
    }

    "sum trns': income, expense, incomes count and expenses count" {
        // Arrange
        val income1 = dummyTrn(type = TrnType.Income, amount = 10.0)
        val income2 = dummyTrn(type = TrnType.Income, amount = 0.50)
        val income3 = dummyTrn(type = TrnType.Income, amount = 100.0)
        val expense1 = dummyTrn(type = TrnType.Expense, amount = 7.50)
        val expense2 = dummyTrn(type = TrnType.Expense, amount = 30.05)
        val trns = listOf(income1, income2, income3, expense1, expense2)
        val arg = TestArg(multiplier = 2)

        // Act
        val res = sumTransactions(
            transactions = trns,
            selectors = nonEmptyListOf(
                ::income,
                ::expense,
                ::countIncome,
                ::countExpense
            ),
            arg = arg
        )
        val income = res[0]
        val expense = res[1]
        val incomesCount = res[2]
        val expensesCount = res[3]

        // Assert:
        // multiplier = 2
        income shouldBe 221.0 // (10 + 0.5 + 100) * 2 = 110.5 * 2 = 221
        expense shouldBe 75.1 // (7.5 + 30.05) * 2 = 37.55 * 2 = 75.1
        incomesCount shouldBe 3
        expensesCount shouldBe 2
    }
})