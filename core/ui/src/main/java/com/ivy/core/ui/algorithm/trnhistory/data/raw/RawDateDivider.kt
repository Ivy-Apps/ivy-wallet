package com.ivy.core.ui.algorithm.trnhistory.data.raw

import com.ivy.core.domain.algorithm.calc.data.RawStats
import java.time.LocalDate

data class RawDateDivider(
    override val id: String,
    val date: LocalDate,
    val cashflow: RawStats,
) : TrnListRawSectionKey