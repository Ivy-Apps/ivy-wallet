package com.ivy.wallet.domain.pure.category

import arrow.core.Option
import arrow.core.toOption
import com.ivy.wallet.domain.data.TransactionType
import com.ivy.wallet.domain.data.entity.Account
import com.ivy.wallet.domain.pure.core.SuspendValueFunction
import com.ivy.wallet.domain.pure.data.FPTransaction
import com.ivy.wallet.domain.pure.exchangeToBaseCurrency
import com.ivy.wallet.io.persistence.dao.ExchangeRateDao
import java.math.BigDecimal
import java.util.*

typealias CategoryValueFunction = SuspendValueFunction<CategoryValueFunctions.Argument>

object CategoryValueFunctions {
    data class Argument(
        val categoryId: UUID?,

        val accounts: List<Account>,
        val exchangeRateDao: ExchangeRateDao,
        val baseCurrencyCode: String,
    )

    suspend fun balance(
        fpTransaction: FPTransaction,
        arg: Argument,
    ): BigDecimal = with(fpTransaction) {
        if (this.categoryId.orNull() == arg.categoryId) {
            when (type) {
                TransactionType.INCOME -> amount.toBaseCurrencyOrZero(arg, accountId)
                TransactionType.EXPENSE -> amount.toBaseCurrencyOrZero(arg, accountId).negate()
                TransactionType.TRANSFER -> BigDecimal.ZERO
            }
        } else BigDecimal.ZERO
    }

    suspend fun income(
        fpTransaction: FPTransaction,
        arg: Argument,
    ): BigDecimal = with(fpTransaction) {
        if (this.categoryId.orNull() == arg.categoryId) {
            when (type) {
                TransactionType.INCOME -> amount.toBaseCurrencyOrZero(arg, accountId)
                else -> BigDecimal.ZERO
            }
        } else BigDecimal.ZERO
    }

    suspend fun expense(
        fpTransaction: FPTransaction,
        arg: Argument,
    ): BigDecimal = with(fpTransaction) {
        if (this.categoryId.orNull() == arg.categoryId) {
            when (type) {
                TransactionType.EXPENSE -> amount.toBaseCurrencyOrZero(arg, accountId)
                else -> BigDecimal.ZERO
            }
        } else BigDecimal.ZERO
    }

    suspend fun incomeCount(
        fpTransaction: FPTransaction,
        arg: Argument,
    ): BigDecimal = with(fpTransaction) {
        if (this.categoryId.orNull() == arg.categoryId) {
            when (type) {
                TransactionType.INCOME -> BigDecimal.ONE
                else -> BigDecimal.ZERO
            }
        } else BigDecimal.ZERO
    }

    suspend fun expenseCount(
        fpTransaction: FPTransaction,
        arg: Argument,
    ): BigDecimal = with(fpTransaction) {
        if (this.categoryId.orNull() == arg.categoryId) {
            when (type) {
                TransactionType.EXPENSE -> BigDecimal.ONE
                else -> BigDecimal.ZERO
            }
        } else BigDecimal.ZERO
    }

    private suspend fun BigDecimal.toBaseCurrencyOrZero(
        argument: Argument,
        accountId: UUID
    ): BigDecimal {
        return this.convertToBaseCurrency(
            argument = argument,
            accountId = accountId
        ).orNull() ?: BigDecimal.ZERO
    }

    private suspend fun BigDecimal.convertToBaseCurrency(
        accountId: UUID,
        argument: Argument
    ): Option<BigDecimal> {
        return exchangeToBaseCurrency(
            exchangeRateDao = argument.exchangeRateDao,
            baseCurrencyCode = argument.baseCurrencyCode,
            fromAmount = this,
            fromCurrencyCode = argument.accounts.find { it.id == accountId }?.currency.toOption()
        )
    }
}