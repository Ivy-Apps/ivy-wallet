package com.ivy.core.domain.pure.dummy

import com.ivy.common.time.dateNowUTC
import com.ivy.data.Value
import com.ivy.data.transaction.TrnListItem
import java.time.LocalDate

fun dummyDateDivider(
    date: LocalDate = dateNowUTC(),
    cashflow: Value = dummyValue()
) = TrnListItem.DateDivider(
    date = date,
    cashflow = cashflow,
)