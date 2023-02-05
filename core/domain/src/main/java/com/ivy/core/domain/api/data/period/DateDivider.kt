package com.ivy.core.domain.api.data.period

import com.ivy.core.data.common.SignedValue
import java.time.LocalDate

data class DateDivider(
    val date: LocalDate,
    val cashflow: SignedValue,
    override val sectionId: String
) : Collapsable, TransactionListItem