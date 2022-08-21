package com.ivy.core.functions.transaction

import com.ivy.data.transaction.TrnListItem
import com.ivy.data.transaction.Value

fun dummyUpcomingSection(
    income: Value = dummyValue(),
    expense: Value = dummyValue()
) = TrnListItem.UpcomingSection(
    income = income,
    expense = expense,
)

fun dummyOverdueSection(
    income: Value = dummyValue(),
    expense: Value = dummyValue()
) = TrnListItem.OverdueSection(
    income = income,
    expense = expense,
)