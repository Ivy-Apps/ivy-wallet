package com.ivy.wallet.domain.fp.wallet

import com.ivy.wallet.domain.data.TransactionHistoryDateDivider
import com.ivy.wallet.domain.data.TransactionHistoryItem
import com.ivy.wallet.domain.data.entity.Transaction
import com.ivy.wallet.domain.fp.core.*
import com.ivy.wallet.domain.fp.data.ClosedTimeRange
import com.ivy.wallet.domain.fp.data.WalletDAOs
import com.ivy.wallet.io.persistence.dao.AccountDao
import com.ivy.wallet.io.persistence.dao.ExchangeRateDao
import com.ivy.wallet.io.persistence.dao.TransactionDao
import com.ivy.wallet.utils.convertUTCtoLocal
import com.ivy.wallet.utils.toEpochSeconds

//TODO: overdue(range)
//TODO: upcoming(range)

suspend fun historyWithDateDividers(
    walletDAOs: WalletDAOs,
    baseCurrencyCode: String,
    range: ClosedTimeRange
): List<TransactionHistoryItem> {
    return history(
        transactionDao = walletDAOs.transactionDao,
        range = range
    ).withDateDividers(
        exchangeRateDao = walletDAOs.exchangeRateDao,
        accountDao = walletDAOs.accountDao,
        baseCurrencyCode = baseCurrencyCode
    )
}

suspend fun history(
    transactionDao: TransactionDao,
    range: ClosedTimeRange
): List<Transaction> {
    return transactionDao.findAllBetween(
        startDate = range.from,
        endDate = range.to
    )
}

suspend fun List<Transaction>.withDateDividers(
    exchangeRateDao: ExchangeRateDao,
    accountDao: AccountDao,
    baseCurrencyCode: String,
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
            val baseCurrencyExchangeData = ExchangeData(
                exchangeRateDao = exchangeRateDao,
                accountDao = accountDao,
                baseCurrencyCode = baseCurrencyCode,
                toCurrency = baseCurrencyCode
            )
            val fpTransactions = transactionsForDate.toFPTransactions()

            listOf<TransactionHistoryItem>(
                TransactionHistoryDateDivider(
                    date = date!!,
                    income = sum(
                        incomes(fpTransactions),
                        ::amountInCurrency,
                        baseCurrencyExchangeData
                    ).toDouble(),
                    expenses = sum(
                        expenses(fpTransactions),
                        ::amountInCurrency,
                        baseCurrencyExchangeData
                    ).toDouble()
                ),
            ).plus(transactionsForDate)
        }
}