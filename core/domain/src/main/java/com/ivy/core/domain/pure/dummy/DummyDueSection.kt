package com.ivy.core.functions.transaction

import com.ivy.data.Value
import com.ivy.data.transaction.OverdueSection
import com.ivy.data.transaction.Transaction
import com.ivy.data.transaction.UpcomingSection

fun dummyUpcomingSection(
    income: Value = com.ivy.core.domain.pure.dummy.dummyValue(),
    expense: Value = com.ivy.core.domain.pure.dummy.dummyValue(),
    upcomingTrns: List<Transaction> = emptyList(),
) = UpcomingSection(
    income = income,
    expense = expense,
    trns = upcomingTrns
)

fun dummyOverdueSection(
    income: Value = com.ivy.core.domain.pure.dummy.dummyValue(),
    expense: Value = com.ivy.core.domain.pure.dummy.dummyValue(),
    overdueTrns: List<Transaction> = emptyList(),
) = OverdueSection(
    income = income,
    expense = expense,
    trns = overdueTrns
)