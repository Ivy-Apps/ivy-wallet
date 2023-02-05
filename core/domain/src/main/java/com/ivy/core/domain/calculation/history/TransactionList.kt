package com.ivy.core.domain.calculation.history

import com.ivy.core.data.Transaction
import com.ivy.core.domain.api.data.period.Collapsable
import com.ivy.core.domain.api.data.period.DateDivider
import com.ivy.core.domain.api.data.period.DueDivider
import com.ivy.core.domain.api.data.period.TransactionListItem
import com.ivy.core.domain.calculation.history.data.Sorted
import java.util.*

fun transactionList(
    due: SortedMap<DueDivider, Sorted<Transaction>>,
    history: SortedMap<DateDivider, Sorted<Transaction>>,
    collapsed: Set<Collapsable>
): List<TransactionListItem> = TODO()