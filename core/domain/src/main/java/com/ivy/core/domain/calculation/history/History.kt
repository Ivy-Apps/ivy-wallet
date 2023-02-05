package com.ivy.core.domain.calculation.history

import com.ivy.core.data.Transaction
import com.ivy.core.data.calculation.ExchangeRates
import com.ivy.core.domain.api.data.period.DateDivider
import com.ivy.core.domain.calculation.history.data.RawDateDivider
import com.ivy.core.domain.calculation.history.data.Sorted
import java.util.*

fun groupHistoryTransactions(
    transactions: List<Transaction>
): SortedMap<RawDateDivider, Sorted<Transaction>> = TODO()

context(ExchangeRates)
fun exchangeHistory(
    rawMap: SortedMap<RawDateDivider, Sorted<Transaction>>
): SortedMap<DateDivider, Sorted<Transaction>> = TODO()