package com.ivy.core.functions.transaction

import com.ivy.data.transaction.OverdueSection
import com.ivy.data.transaction.Transaction
import com.ivy.data.transaction.UpcomingSection
import com.ivy.data.transaction.Value

fun dummyUpcomingSection(
    income: Value = com.ivy.core.domain.functions.dummy.dummyValue(),
    expense: Value = com.ivy.core.domain.functions.dummy.dummyValue(),
    upcomingTrns: List<Transaction> = emptyList(),
) = UpcomingSection(
    income = income,
    expense = expense,
    trns = upcomingTrns
)

fun dummyOverdueSection(
    income: Value = com.ivy.core.domain.functions.dummy.dummyValue(),
    expense: Value = com.ivy.core.domain.functions.dummy.dummyValue(),
    overdueTrns: List<Transaction> = emptyList(),
) = OverdueSection(
    income = income,
    expense = expense,
    trns = overdueTrns
)