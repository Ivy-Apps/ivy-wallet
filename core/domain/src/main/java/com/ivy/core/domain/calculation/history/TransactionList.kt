package com.ivy.core.domain.calculation.history

import com.ivy.core.data.Transaction
import com.ivy.core.domain.api.data.Collapsable
import com.ivy.core.domain.api.data.DateDivider
import com.ivy.core.domain.api.data.DueDivider
import com.ivy.core.domain.api.data.TransactionListItem
import com.ivy.core.domain.calculation.history.data.Sorted
import java.util.*

fun transactionList(
    due: SortedMap<DueDivider, Sorted<Transaction>>,
    history: SortedMap<DateDivider, Sorted<Transaction>>,
    collapsed: Set<Collapsable>
): List<TransactionListItem> = TODO()