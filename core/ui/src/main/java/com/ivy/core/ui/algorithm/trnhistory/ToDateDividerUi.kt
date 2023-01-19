package com.ivy.core.ui.algorithm.trnhistory

import android.content.Context
import com.ivy.common.time.format
import com.ivy.core.domain.algorithm.calc.exchangeRawStats
import com.ivy.core.domain.pure.format.SignedValueUi
import com.ivy.core.domain.pure.format.format
import com.ivy.core.ui.R
import com.ivy.core.ui.algorithm.trnhistory.data.DateDividerUi
import com.ivy.core.ui.algorithm.trnhistory.data.raw.RawDateDivider
import com.ivy.data.Value
import com.ivy.data.exchange.ExchangeRates
import java.time.LocalDate
import kotlin.math.absoluteValue

suspend fun toDateDividerUi(
    appContext: Context,
    raw: RawDateDivider,
    collapsed: Boolean,
    rates: ExchangeRates,
    today: LocalDate,
): DateDividerUi {
    val stats = exchangeRawStats(raw.cashflow, rates, rates.baseCurrency)
    val cashFlowAmount = stats.income.amount - stats.expense.amount

    val cashflowUi = format(
        Value(cashFlowAmount.absoluteValue, rates.baseCurrency),
        shortenFiat = true
    )
    return DateDividerUi(
        id = raw.id,
        date = raw.date.format(
            if (today.year == raw.date.year) "MMMM dd." else "MMM dd, yyyy"
        ),
        dateContext = when (raw.date) {
            today -> appContext.getString(R.string.today)
            today.minusDays(1) -> appContext.getString(R.string.yesterday)
            today.plusDays(1) -> appContext.getString(R.string.tomorrow)
            else -> null
        } ?: raw.date.format("EEEE"),
        cashflow = when {
            cashFlowAmount > 0 -> SignedValueUi.Positive(cashflowUi)
            cashFlowAmount < 0 -> SignedValueUi.Negative(cashflowUi)
            else -> SignedValueUi.Zero(cashflowUi)
        },
        collapsed = collapsed,
    )
}