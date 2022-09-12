package com.ivy.wallet.domain.pure.transaction

import com.ivy.data.AccountOld
import com.ivy.data.transaction.TransactionOld
import com.ivy.data.transaction.TrnTypeOld
import com.ivy.frp.SideEffect
import com.ivy.wallet.domain.pure.exchange.ExchangeEffect
import com.ivy.wallet.domain.pure.exchange.exchangeInBaseCurrency
import java.math.BigDecimal

object WalletValueFunctions {
    data class Argument(
        val accounts: List<AccountOld>,
        val baseCurrency: String,

        @SideEffect
        val exchange: ExchangeEffect
    )

    suspend fun income(
        transaction: TransactionOld,
        arg: Argument
    ): BigDecimal = with(transaction) {
        when (type) {
            TrnTypeOld.INCOME -> exchangeInBaseCurrency(
                transaction = this,
                accounts = arg.accounts,
                baseCurrency = arg.baseCurrency,
                exchange = arg.exchange
            )
            else -> BigDecimal.ZERO
        }
    }

    suspend fun transferIncome(
        transaction: TransactionOld,
        arg: Argument
    ): BigDecimal = with(transaction) {
        val condition = arg.accounts.any { it.id == this.toAccountId }
        when {
            type == TrnTypeOld.TRANSFER && condition ->
                exchangeInBaseCurrency(
                    transaction = this.copy(
                        amount = this.toAmount,
                        accountId = this.toAccountId ?: this.accountId
                    ), //Do not remove copy()
                    accounts = arg.accounts,
                    baseCurrency = arg.baseCurrency,
                    exchange = arg.exchange
                )
            else -> BigDecimal.ZERO
        }
    }

    suspend fun expense(
        transaction: TransactionOld,
        arg: Argument
    ): BigDecimal = with(transaction) {
        when (type) {
            TrnTypeOld.EXPENSE -> exchangeInBaseCurrency(
                transaction = this,
                accounts = arg.accounts,
                baseCurrency = arg.baseCurrency,
                exchange = arg.exchange
            )
            else -> BigDecimal.ZERO
        }
    }

    suspend fun transferExpenses(
        transaction: TransactionOld,
        arg: Argument
    ): BigDecimal = with(transaction) {
        val condition = arg.accounts.any { it.id == this.accountId }
        when {
            type == TrnTypeOld.TRANSFER && condition -> exchangeInBaseCurrency(
                transaction = this,
                accounts = arg.accounts,
                baseCurrency = arg.baseCurrency,
                exchange = arg.exchange
            )
            else -> BigDecimal.ZERO
        }
    }
}