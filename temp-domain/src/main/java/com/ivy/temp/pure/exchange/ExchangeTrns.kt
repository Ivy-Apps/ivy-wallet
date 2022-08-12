package com.ivy.wallet.domain.pure.exchange

import arrow.core.Option
import arrow.core.toOption
import com.ivy.data.AccountOld
import com.ivy.data.transaction.TransactionOld
import com.ivy.exchange.deprecated.ExchangeData
import com.ivy.frp.Pure
import com.ivy.frp.SideEffect
import com.ivy.wallet.domain.pure.account.accountCurrency
import com.ivy.wallet.domain.pure.transaction.trnCurrency
import java.math.BigDecimal
import java.util.*

typealias ExchangeEffect = suspend (ExchangeData, BigDecimal) -> Option<BigDecimal>

data class ExchangeTrnArgument(
    val baseCurrency: String,
    @SideEffect
    val getAccount: suspend (accountId: UUID) -> AccountOld?,
    @SideEffect
    val exchange: ExchangeEffect
)

@Pure
suspend fun exchangeInBaseCurrency(
    transaction: TransactionOld,
    arg: ExchangeTrnArgument
): BigDecimal {
    val fromCurrency = arg.getAccount(transaction.accountId)?.let {
        accountCurrency(it, arg.baseCurrency)
    }.toOption()

    return exchangeInCurrency(
        transaction = transaction,
        baseCurrency = arg.baseCurrency,
        trnCurrency = fromCurrency,
        toCurrency = arg.baseCurrency,
        exchange = arg.exchange
    )
}

@Pure
suspend fun exchangeInBaseCurrency(
    transaction: TransactionOld,
    baseCurrency: String,
    accounts: List<AccountOld>,

    @SideEffect
    exchange: ExchangeEffect
): BigDecimal = exchangeInCurrency(
    transaction = transaction,
    baseCurrency = baseCurrency,
    accounts = accounts,
    toCurrency = baseCurrency,
    exchange = exchange
)

@Pure
suspend fun exchangeInCurrency(
    transaction: TransactionOld,
    baseCurrency: String,
    accounts: List<AccountOld>,
    toCurrency: String,

    @SideEffect
    exchange: ExchangeEffect
): BigDecimal {
    return exchange(
        ExchangeData(
            baseCurrency = baseCurrency,
            fromCurrency = trnCurrency(transaction, accounts, baseCurrency),
            toCurrency = toCurrency
        ),
        transaction.amount
    ).orNull() ?: BigDecimal.ZERO
}

suspend fun exchangeInCurrency(
    transaction: TransactionOld,
    baseCurrency: String,
    trnCurrency: Option<String>,
    toCurrency: String,

    @SideEffect
    exchange: ExchangeEffect
): BigDecimal {
    return exchange(
        ExchangeData(
            baseCurrency = baseCurrency,
            fromCurrency = trnCurrency,
            toCurrency = toCurrency
        ),
        transaction.amount
    ).orNull() ?: BigDecimal.ZERO
}