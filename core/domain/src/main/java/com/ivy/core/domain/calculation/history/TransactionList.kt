package com.ivy.core.domain.calculation.history

import com.ivy.core.data.Transaction
import com.ivy.core.domain.calculation.history.data.*
import java.util.*

fun transactionList(
    due: SortedMap<DueDivider, Sorted<Transaction>>,
    history: SortedMap<DateDivider, Sorted<Transaction>>,
    collapsed: Set<Collapsable>
): List<TransactionListItem> = TODO()