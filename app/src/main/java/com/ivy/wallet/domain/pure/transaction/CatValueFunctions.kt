package com.ivy.wallet.domain.pure.transaction

import arrow.core.Option
import arrow.core.toOption
import com.ivy.frp.SideEffect
import com.ivy.wallet.domain.data.TransactionType
import com.ivy.wallet.domain.data.core.Account
import com.ivy.wallet.domain.data.core.Transaction
import java.math.BigDecimal
import java.util.*

typealias CategoryValueFunction = SuspendValueFunction<CategoryValueFunctions.Argument>

object CategoryValueFunctions {
    data class Argument(
        val categoryId: UUID?,
        val accounts: List<Account>,

        @SideEffect
        val exchangeToBaseCurrency: suspend (
            fromCurrency: Option<String>,
            amount: BigDecimal
        ) -> Option<BigDecimal>
    )

    suspend fun balance(
        transaction: Transaction,
        arg: Argument,
    ): BigDecimal = with(transaction) {
        if (this.categoryId == arg.categoryId) {
            when (type) {
                TransactionType.INCOME -> amount.toBaseCurrencyOrZero(arg, accountId)
                TransactionType.EXPENSE -> amount.toBaseCurrencyOrZero(arg, accountId).negate()
                TransactionType.TRANSFER -> BigDecimal.ZERO
            }
        } else BigDecimal.ZERO
    }

    suspend fun income(
        transaction: Transaction,
        arg: Argument,
    ): BigDecimal = with(transaction) {
        if (this.categoryId == arg.categoryId) {
            when (type) {
                TransactionType.INCOME -> amount.toBaseCurrencyOrZero(arg, accountId)
                else -> BigDecimal.ZERO
            }
        } else BigDecimal.ZERO
    }

    suspend fun expense(
        transaction: Transaction,
        arg: Argument,
    ): BigDecimal = with(transaction) {
        if (this.categoryId == arg.categoryId) {
            when (type) {
                TransactionType.EXPENSE -> amount.toBaseCurrencyOrZero(arg, accountId)
                else -> BigDecimal.ZERO
            }
        } else BigDecimal.ZERO
    }

    suspend fun incomeCount(
        transaction: Transaction,
        arg: Argument,
    ): BigDecimal = with(transaction) {
        if (this.categoryId == arg.categoryId) {
            when (type) {
                TransactionType.INCOME -> BigDecimal.ONE
                else -> BigDecimal.ZERO
            }
        } else BigDecimal.ZERO
    }

    suspend fun expenseCount(
        transaction: Transaction,
        arg: Argument,
    ): BigDecimal = with(transaction) {
        if (this.categoryId == arg.categoryId) {
            when (type) {
                TransactionType.EXPENSE -> BigDecimal.ONE
                else -> BigDecimal.ZERO
            }
        } else BigDecimal.ZERO
    }

    private suspend fun BigDecimal.toBaseCurrencyOrZero(
        arg: Argument,
        accountId: UUID
    ): BigDecimal {
        return this.convertToBaseCurrency(
            arg = arg,
            accountId = accountId
        ).orNull() ?: BigDecimal.ZERO
    }

    private suspend fun BigDecimal.convertToBaseCurrency(
        accountId: UUID,
        arg: Argument
    ): Option<BigDecimal> {
        val trnCurrency = arg.accounts.find { it.id == accountId }?.currency.toOption()
        return arg.exchangeToBaseCurrency(trnCurrency, this)
    }
}