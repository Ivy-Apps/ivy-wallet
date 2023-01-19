package com.ivy.core.ui.algorithm.trnhistory

import com.ivy.core.domain.algorithm.calc.exchangeRawStats
import com.ivy.core.domain.pure.format.ValueUi
import com.ivy.core.domain.pure.format.format
import com.ivy.core.ui.R
import com.ivy.core.ui.algorithm.trnhistory.data.DueDividerUi
import com.ivy.core.ui.algorithm.trnhistory.data.DueDividerUiType
import com.ivy.core.ui.algorithm.trnhistory.data.raw.RawDividerType
import com.ivy.core.ui.algorithm.trnhistory.data.raw.RawDueDivider
import com.ivy.data.Value
import com.ivy.data.exchange.ExchangeRates

suspend fun toDueDividerUi(
    raw: RawDueDivider,
    collapsed: Boolean,
    rates: ExchangeRates,
    getString: (Int) -> String
): DueDividerUi {
    fun dueValueUi(value: Value): ValueUi? = value.takeIf { it.amount > 0 }?.let {
        format(it, shortenFiat = true)
    }

    val stats = exchangeRawStats(raw.rawStats, rates, rates.baseCurrency)

    return DueDividerUi(
        id = raw.id,
        income = dueValueUi(stats.income),
        expense = dueValueUi(stats.expense),
        label = getString(
            when (raw.type) {
                RawDividerType.Upcoming -> R.string.upcoming
                RawDividerType.Overdue -> R.string.overdue
            }
        ),
        type = when (raw.type) {
            RawDividerType.Upcoming -> DueDividerUiType.Upcoming
            RawDividerType.Overdue -> DueDividerUiType.Overdue
        },
        collapsed = collapsed,
    )
}