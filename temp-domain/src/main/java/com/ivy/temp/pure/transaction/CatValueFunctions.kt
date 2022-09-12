package com.ivy.wallet.domain.pure.transaction

import arrow.core.Option
import arrow.core.toOption
import com.ivy.data.AccountOld
import com.ivy.data.transaction.TransactionOld
import com.ivy.data.transaction.TrnTypeOld
import com.ivy.frp.SideEffect
import java.math.BigDecimal
import java.util.*

typealias CategoryValueFunction = SuspendValueFunction<CategoryValueFunctions.Argument>

object CategoryValueFunctions {
    data class Argument(
        val categoryId: UUID?,
        val accounts: List<AccountOld>,

        @SideEffect
        val exchangeToBaseCurrency: suspend (
            fromCurrency: Option<String>,
            amount: BigDecimal
        ) -> Option<BigDecimal>
    )

    suspend fun balance(
        transaction: TransactionOld,
        arg: Argument,
    ): BigDecimal = with(transaction) {
        if (this.categoryId == arg.categoryId) {
            when (type) {
                TrnTypeOld.INCOME -> amount.toBaseCurrencyOrZero(arg, accountId)
                TrnTypeOld.EXPENSE -> amount.toBaseCurrencyOrZero(arg, accountId).negate()
                TrnTypeOld.TRANSFER -> BigDecimal.ZERO
            }
        } else BigDecimal.ZERO
    }

    suspend fun income(
        transaction: TransactionOld,
        arg: Argument,
    ): BigDecimal = with(transaction) {
        if (this.categoryId == arg.categoryId) {
            when (type) {
                TrnTypeOld.INCOME -> amount.toBaseCurrencyOrZero(arg, accountId)
                else -> BigDecimal.ZERO
            }
        } else BigDecimal.ZERO
    }

    suspend fun expense(
        transaction: TransactionOld,
        arg: Argument,
    ): BigDecimal = with(transaction) {
        if (this.categoryId == arg.categoryId) {
            when (type) {
                TrnTypeOld.EXPENSE -> amount.toBaseCurrencyOrZero(arg, accountId)
                else -> BigDecimal.ZERO
            }
        } else BigDecimal.ZERO
    }

    suspend fun incomeCount(
        transaction: TransactionOld,
        arg: Argument,
    ): BigDecimal = with(transaction) {
        if (this.categoryId == arg.categoryId) {
            when (type) {
                TrnTypeOld.INCOME -> BigDecimal.ONE
                else -> BigDecimal.ZERO
            }
        } else BigDecimal.ZERO
    }

    suspend fun expenseCount(
        transaction: TransactionOld,
        arg: Argument,
    ): BigDecimal = with(transaction) {
        if (this.categoryId == arg.categoryId) {
            when (type) {
                TrnTypeOld.EXPENSE -> BigDecimal.ONE
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