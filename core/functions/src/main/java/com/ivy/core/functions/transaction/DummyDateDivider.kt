package com.ivy.core.functions.transaction

import com.ivy.common.dateNowUTC
import com.ivy.data.transaction.TrnListItem
import com.ivy.data.transaction.Value
import java.time.LocalDate

fun dummyDateDivider(
    date: LocalDate = dateNowUTC(),
    cashflow: Value = dummyValue()
) = TrnListItem.DateDivider(
    date = date,
    cashflow = cashflow,
)