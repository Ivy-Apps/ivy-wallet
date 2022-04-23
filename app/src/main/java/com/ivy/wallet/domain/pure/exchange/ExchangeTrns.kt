package com.ivy.wallet.domain.pure.exchange

import arrow.core.Option
import arrow.core.toOption
import com.ivy.fp.SideEffect
import com.ivy.wallet.domain.data.core.Account
import com.ivy.wallet.domain.data.core.Transaction
import java.math.BigDecimal
import java.util.*


data class ExchangeTrnArgument(
    val baseCurrency: String,
    @SideEffect
    val getAccount: suspend (accountId: UUID) -> Account?,
    @SideEffect
    val exchange: suspend (ExchangeData, BigDecimal) -> Option<BigDecimal>
)

suspend fun exchangeInCurrency(
    transaction: Transaction,
    arg: ExchangeTrnArgument
): BigDecimal {
    val fromCurrencyCode = arg.getAccount(transaction.accountId)?.currency.toOption()

    return arg.exchange(
        ExchangeData(
            baseCurrency = arg.baseCurrency,
            fromCurrency = fromCurrencyCode,
            toCurrency = arg.baseCurrency
        ),
        transaction.amount
    ).orNull() ?: BigDecimal.ZERO
}