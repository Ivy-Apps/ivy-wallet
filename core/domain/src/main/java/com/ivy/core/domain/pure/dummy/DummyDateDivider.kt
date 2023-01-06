package com.ivy.core.domain.pure.dummy

import com.ivy.common.time.dateNowUTC
import com.ivy.data.Value
import com.ivy.data.transaction.TrnListItem
import java.time.LocalDate

fun dummyDateDivider(
    id: String = "01-01-2023",
    date: LocalDate = dateNowUTC(),
    cashflow: Value = dummyValue(),
    collapsed: Boolean = false,
) = TrnListItem.DateDivider(
    id = id,
    date = date,
    cashflow = cashflow,
    collapsed = collapsed,
)