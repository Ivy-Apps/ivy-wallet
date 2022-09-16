package com.ivy.wallet.domain.pure.transaction

import arrow.core.Option
import arrow.core.toOption
import com.ivy.base.TransactionHistoryDateDivider
import com.ivy.common.convertUTCtoLocal
import com.ivy.common.toEpochSeconds
import com.ivy.data.AccountOld
import com.ivy.data.transaction.TransactionOld
import com.ivy.frp.Pure
import com.ivy.frp.SideEffect
import com.ivy.frp.then
import com.ivy.temp.persistence.ExchangeData
import com.ivy.wallet.domain.deprecated.logic.currency.ExchangeRatesLogic
import com.ivy.wallet.domain.pure.exchange.ExchangeTrnArgument
import com.ivy.wallet.domain.pure.exchange.exchangeInBaseCurrency
import com.ivy.wallet.io.persistence.dao.AccountDao
import com.ivy.wallet.io.persistence.dao.SettingsDao
import java.math.BigDecimal
import java.util.*

@Deprecated("Migrate to actions")
suspend fun List<TransactionOld>.withDateDividers(
    exchangeRatesLogic: ExchangeRatesLogic,
    settingsDao: SettingsDao,
    accountDao: AccountDao
): List<Any> {
    return transactionsWithDateDividers(
        transactions = this,
        baseCurrencyCode = settingsDao.findFirstSuspend().currency,
        getAccount = accountDao::findById then { it?.toDomain() },
        exchange = { data, amount ->
            exchangeRatesLogic.convertAmount(
                baseCurrency = data.baseCurrency,
                fromCurrency = data.fromCurrency.orNull() ?: "",
                toCurrency = data.toCurrency,
                amount = amount.toDouble()
            ).toBigDecimal().toOption()
        }
    )
}

@Deprecated("old")
@Pure
suspend fun transactionsWithDateDividers(
    transactions: List<TransactionOld>,
    baseCurrencyCode: String,

    @SideEffect
    getAccount: suspend (accountId: UUID) -> AccountOld?,
    @SideEffect
    exchange: suspend (ExchangeData, BigDecimal) -> Option<BigDecimal>
): List<Any> {
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


            listOf<Any>(
                TransactionHistoryDateDivider(
                    date = date!!,
                    income = sumTrns(
                        incomes(transactionsForDate),
                        ::exchangeInBaseCurrency,
                        arg
                    ).toDouble(),
                    expenses = sumTrns(
                        expenses(transactionsForDate),
                        ::exchangeInBaseCurrency,
                        arg
                    ).toDouble()
                ),
            ).plus(transactionsForDate)
        }
}