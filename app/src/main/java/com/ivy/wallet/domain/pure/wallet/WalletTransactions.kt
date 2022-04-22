package com.ivy.wallet.domain.pure.wallet

import arrow.core.Option
import arrow.core.toOption
import com.ivy.fp.Pure
import com.ivy.fp.SideEffect
import com.ivy.wallet.domain.data.TransactionHistoryDateDivider
import com.ivy.wallet.domain.data.TransactionHistoryItem
import com.ivy.wallet.domain.data.core.Account
import com.ivy.wallet.domain.data.core.Transaction
import com.ivy.wallet.domain.pure.ExchangeData
import com.ivy.wallet.domain.pure.core.expenses
import com.ivy.wallet.domain.pure.core.incomes
import com.ivy.wallet.domain.pure.core.sum
import com.ivy.wallet.domain.pure.core.toFPTransactions
import com.ivy.wallet.domain.pure.data.FPTransaction
import com.ivy.wallet.utils.convertUTCtoLocal
import com.ivy.wallet.utils.toEpochSeconds
import java.math.BigDecimal
import java.util.*

//TODO: overdue(range)
//TODO: upcoming(range)

@Pure
suspend fun List<Transaction>.withDateDividers(
    baseCurrencyCode: String,

    @SideEffect
    getAccount: suspend (accountId: UUID) -> Account?,
    @SideEffect
    exchange: suspend (ExchangeData, BigDecimal) -> Option<BigDecimal>
): List<TransactionHistoryItem> {
    val history = this
    if (history.isEmpty()) return emptyList()

    return history
        .groupBy { it.dateTime?.convertUTCtoLocal()?.toLocalDate() }
        .filterKeys { it != null }
        .toSortedMap { date1, date2 ->
            if (date1 == null || date2 == null) return@toSortedMap 0 //this case shouldn't happen
            (date2.atStartOfDay().toEpochSeconds() - date1.atStartOfDay().toEpochSeconds()).toInt()
        }
        .flatMap { (date, transactionsForDate) ->
            val arg = AmountInCurrencyArgument(
                baseCurrency = baseCurrencyCode,
                getAccount = getAccount,
                exchange = exchange
            )

            val fpTransactions = transactionsForDate.toFPTransactions()

            listOf<TransactionHistoryItem>(
                TransactionHistoryDateDivider(
                    date = date!!,
                    income = sum(
                        incomes(fpTransactions),
                        ::amountInCurrency,
                        arg
                    ).toDouble(),
                    expenses = sum(
                        expenses(fpTransactions),
                        ::amountInCurrency,
                        arg
                    ).toDouble()
                ),
            ).plus(transactionsForDate)
        }
}

data class AmountInCurrencyArgument(
    val baseCurrency: String,
    @SideEffect
    val getAccount: suspend (accountId: UUID) -> Account?,
    @SideEffect
    val exchange: suspend (ExchangeData, BigDecimal) -> Option<BigDecimal>
)

suspend fun amountInCurrency(
    fpTransaction: FPTransaction,
    arg: AmountInCurrencyArgument
): BigDecimal {
    val fromCurrencyCode = arg.getAccount(fpTransaction.accountId)?.currency.toOption()

    return arg.exchange(
        ExchangeData(
            baseCurrency = arg.baseCurrency,
            fromCurrency = fromCurrencyCode,
            toCurrency = arg.baseCurrency
        ),
        fpTransaction.amount
    ).orNull() ?: BigDecimal.ZERO
}