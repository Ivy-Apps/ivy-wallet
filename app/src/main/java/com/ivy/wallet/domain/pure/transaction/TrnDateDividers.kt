package com.ivy.wallet.domain.pure.transaction

import arrow.core.Option
import com.ivy.fp.Pure
import com.ivy.fp.SideEffect
import com.ivy.wallet.domain.data.TransactionHistoryDateDivider
import com.ivy.wallet.domain.data.TransactionHistoryItem
import com.ivy.wallet.domain.data.core.Account
import com.ivy.wallet.domain.data.core.Transaction
import com.ivy.wallet.domain.pure.exchange.ExchangeData
import com.ivy.wallet.domain.pure.exchange.ExchangeTrnArgument
import com.ivy.wallet.domain.pure.exchange.exchangeInCurrency
import com.ivy.wallet.utils.convertUTCtoLocal
import com.ivy.wallet.utils.toEpochSeconds
import java.math.BigDecimal
import java.util.*

@Pure
suspend fun transactionsWithDateDividers(
    transactions: List<Transaction>,
    baseCurrencyCode: String,

    @SideEffect
    getAccount: suspend (accountId: UUID) -> Account?,
    @SideEffect
    exchange: suspend (ExchangeData, BigDecimal) -> Option<BigDecimal>
): List<TransactionHistoryItem> {
    if (transactions.isEmpty()) return emptyList()

    return transactions
        .groupBy { it.dateTime?.convertUTCtoLocal()?.toLocalDate() }
        .filterKeys { it != null }
        .toSortedMap { date1, date2 ->
            if (date1 == null || date2 == null) return@toSortedMap 0 //this case shouldn't happen
            (date2.atStartOfDay().toEpochSeconds() - date1.atStartOfDay().toEpochSeconds()).toInt()
        }
        .flatMap { (date, transactionsForDate) ->
            val arg = ExchangeTrnArgument(
                baseCurrency = baseCurrencyCode,
                getAccount = getAccount,
                exchange = exchange
            )


            listOf<TransactionHistoryItem>(
                TransactionHistoryDateDivider(
                    date = date!!,
                    income = sumTrns(
                        incomes(transactionsForDate),
                        ::exchangeInCurrency,
                        arg
                    ).toDouble(),
                    expenses = sumTrns(
                        expenses(transactionsForDate),
                        ::exchangeInCurrency,
                        arg
                    ).toDouble()
                ),
            ).plus(transactionsForDate)
        }
}